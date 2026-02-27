#!/usr/bin/env bash
set -euo pipefail

MODE=""
OLD_REF=""
NEW_REF=""
MAX_FILE_SIZE_KB="512"

RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m'

usage() {
  cat <<'USAGE'
Usage:
  git-transfer-audit.sh --mode push --old <ref> --new <ref> [--max-size-kb <kb>]
  git-transfer-audit.sh --mode pull --old <ref> --new <ref> [--max-size-kb <kb>]

Checks files changed between two refs before/after pull/push operations.
Fails with non-zero exit code if high-risk patterns are found.
USAGE
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --mode)
      MODE="$2"
      shift 2
      ;;
    --old)
      OLD_REF="$2"
      shift 2
      ;;
    --new)
      NEW_REF="$2"
      shift 2
      ;;
    --max-size-kb)
      MAX_FILE_SIZE_KB="$2"
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1"
      usage
      exit 2
      ;;
  esac
done

if [[ -z "$MODE" || -z "$OLD_REF" || -z "$NEW_REF" ]]; then
  usage
  exit 2
fi

if ! [[ "$MODE" =~ ^(push|pull)$ ]]; then
  echo "Invalid mode: $MODE"
  exit 2
fi

if [[ "$OLD_REF" =~ ^0+$ ]]; then
  OLD_REF="$(git hash-object -t tree /dev/null)"
fi

if ! git rev-parse --verify "$OLD_REF" >/dev/null 2>&1; then
  echo "Could not resolve old ref: $OLD_REF"
  exit 2
fi

if ! git rev-parse --verify "$NEW_REF" >/dev/null 2>&1; then
  echo "Could not resolve new ref: $NEW_REF"
  exit 2
fi

mapfile -t CHANGED_FILES < <(git diff --name-only --diff-filter=ACMR "$OLD_REF" "$NEW_REF")

if [[ ${#CHANGED_FILES[@]} -eq 0 ]]; then
  echo -e "${GREEN}[audit] No changed files detected for $MODE audit.${NC}"
  exit 0
fi

echo -e "${YELLOW}[audit] Running $MODE audit for ${#CHANGED_FILES[@]} changed file(s)...${NC}"

SECRET_REGEX='(AKIA[0-9A-Z]{16}|-----BEGIN (RSA|EC|OPENSSH|PGP)? ?PRIVATE KEY-----|xox[baprs]-[0-9A-Za-z-]{10,}|AIza[0-9A-Za-z_-]{35}|ghp_[0-9A-Za-z]{36}|password\s*[:=])'
BINARY_EXT_REGEX='\.(exe|dll|so|dylib|jar|keystore|p12|pfx)$'

errors=0
warnings=0

for file in "${CHANGED_FILES[@]}"; do
  if ! git cat-file -e "$NEW_REF:$file" 2>/dev/null; then
    continue
  fi

  blob="$(git show "$NEW_REF:$file")"
  size_bytes="$(git cat-file -s "$NEW_REF:$file")"
  size_kb=$(( (size_bytes + 1023) / 1024 ))

  if [[ "$size_kb" -gt "$MAX_FILE_SIZE_KB" ]]; then
    echo -e "${RED}[ERROR] $file is ${size_kb}KB (limit ${MAX_FILE_SIZE_KB}KB).${NC}"
    ((errors+=1))
  fi

  if [[ "$file" =~ $BINARY_EXT_REGEX ]]; then
    echo -e "${RED}[ERROR] $file is a blocked binary-like extension.${NC}"
    ((errors+=1))
  fi

  if [[ "$file" =~ (^|/)\.env(\.|$) || "$file" =~ (^|/)id_rsa(\.|$) ]]; then
    echo -e "${RED}[ERROR] $file appears to be a sensitive file name.${NC}"
    ((errors+=1))
  fi

  if printf '%s' "$blob" | rg -n --pcre2 "$SECRET_REGEX" >/dev/null 2>&1; then
    echo -e "${RED}[ERROR] Possible secret pattern found in $file.${NC}"
    ((errors+=1))
  fi

  if [[ "$file" =~ ^(src/main/resources|config)/ && "$file" =~ (application|settings).*(\.ya?ml|\.properties|\.json)$ ]]; then
    if printf '%s' "$blob" | rg -n --pcre2 '(?i)(password|secret|token|apikey)\s*[:=]\s*[^$\{\s]+' >/dev/null 2>&1; then
      echo -e "${RED}[ERROR] Hard-coded config credential detected in $file.${NC}"
      ((errors+=1))
    fi
  fi

  if [[ "$file" =~ \.(sql|sh)$ ]]; then
    if printf '%s' "$blob" | rg -n '(DROP\s+TABLE|TRUNCATE\s+TABLE|rm\s+-rf\s+/)' >/dev/null 2>&1; then
      echo -e "${YELLOW}[WARN] Destructive command pattern found in $file. Please review.${NC}"
      ((warnings+=1))
    fi
  fi
done

if [[ "$warnings" -gt 0 ]]; then
  echo -e "${YELLOW}[audit] $warnings warning(s) detected.${NC}"
fi

if [[ "$errors" -gt 0 ]]; then
  echo -e "${RED}[audit] FAILED: $errors error(s) detected.${NC}"
  exit 1
fi

echo -e "${GREEN}[audit] PASSED: no blocking issues detected.${NC}"
