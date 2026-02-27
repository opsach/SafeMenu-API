# 🛡️ SafeMenu API

**Digital Menu & Allergen Compliance Platform**

A Spring Boot REST API for managing restaurant menus with **automatic EU-14 allergen detection**. Restaurants add dishes and ingredients — the system auto-computes allergen profiles and lets customers filter menus by their specific allergies.

Built to demonstrate real-world API design with **domain-driven business logic**, not just CRUD.

[![CI](https://github.com/opsach/SafeMenu-API/actions/workflows/ci.yml/badge.svg)](https://github.com/opsach/SafeMenu-API/actions/workflows/ci.yml)

---

## 🎯 The Problem

EU Regulation No 1169/2011 requires all food businesses to declare **14 major allergens** on their menus. Most restaurants manage this manually in spreadsheets — error-prone, slow to update, and a legal liability.

## 💡 The Solution

SafeMenu API automates allergen compliance:

1. **Ingredients** are tagged with their EU-14 allergens (e.g. "Butter" → `MILK`)
2. **Dishes** are composed of ingredients
3. **Allergens are auto-computed** — the API aggregates all allergens from a dish's ingredients
4. **Customers can filter** — `GET /api/v1/dishes/safe?exclude=MILK,NUTS` returns only safe dishes

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────┐
│                     REST Controllers                      │
│  /restaurants  /categories  /ingredients  /dishes         │
├──────────────────────────────────────────────────────────┤
│                    Service Layer                          │
│  Business logic • Allergen computation • Caching         │
├──────────────────────────────────────────────────────────┤
│                  Spring Data JPA                          │
│  Custom queries • Safe-dish filtering • Pagination       │
├──────────────────────────────────────────────────────────┤
│                PostgreSQL / H2                            │
│  Flyway migrations • Seed data                           │
└──────────────────────────────────────────────────────────┘
```

### Entity Relationships

```
Restaurant ─┐
             ├── MenuCategory ─── Dish ──┐
             └── MenuCategory ─── Dish   ├──── Ingredient ─── AllergenType[]
                                  Dish ──┘
```

---

## 🚀 Quick Start

### Option 1: Docker (recommended)

```bash
docker compose up --build
```

API available at `http://localhost:8080` with seed data loaded.

### Option 2: Local (H2 in-memory)

```bash
./mvnw spring-boot:run
```

Starts with H2 database and demo data — zero config needed.

### Explore the API

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI Spec | http://localhost:8080/api-docs |
| H2 Console (dev) | http://localhost:8080/h2-console |

---

## 📡 API Endpoints

### Restaurants
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/restaurants` | List all restaurants |
| `GET` | `/api/v1/restaurants/{id}` | Get restaurant details |
| `POST` | `/api/v1/restaurants` | Create restaurant |
| `PUT` | `/api/v1/restaurants/{id}` | Update restaurant |
| `DELETE` | `/api/v1/restaurants/{id}` | Delete restaurant |

### Menu Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/categories/restaurant/{restaurantId}` | List categories (sorted) |
| `POST` | `/api/v1/categories` | Create category |
| `PUT` | `/api/v1/categories/{id}` | Update category |
| `DELETE` | `/api/v1/categories/{id}` | Delete category |

### Ingredients
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/ingredients` | List all ingredients |
| `GET` | `/api/v1/ingredients/search?query=milk` | Search ingredients |
| `POST` | `/api/v1/ingredients` | Create with allergen tags |
| `PUT` | `/api/v1/ingredients/{id}` | Update ingredient |
| `DELETE` | `/api/v1/ingredients/{id}` | Delete ingredient |

### Dishes ⭐
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/dishes/{id}` | Dish with full allergen breakdown |
| `GET` | `/api/v1/dishes/restaurant/{id}` | Paginated full menu |
| `GET` | `/api/v1/dishes/category/{id}` | Dishes in a category |
| `GET` | `/api/v1/dishes/safe?restaurantId=1&exclude=MILK,NUTS` | **🛡️ Allergen-safe dishes** |
| `GET` | `/api/v1/dishes/restaurant/{id}/vegetarian` | Vegetarian dishes |
| `GET` | `/api/v1/dishes/restaurant/{id}/vegan` | Vegan dishes |
| `POST` | `/api/v1/dishes` | Create dish (allergens auto-computed) |
| `PUT` | `/api/v1/dishes/{id}` | Update dish |
| `PATCH` | `/api/v1/dishes/{id}/toggle-availability` | 86 a dish / bring it back |
| `DELETE` | `/api/v1/dishes/{id}` | Delete dish |

---

## 🔬 Core Feature: Auto-Computed Allergens

When you create a dish with ingredients, the API automatically computes all applicable allergens:

```bash
# Create a dish with butter (MILK) and flour (CEREALS_WITH_GLUTEN)
curl -X POST http://localhost:8080/api/v1/dishes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pancakes",
    "price": 12.50,
    "categoryId": 1,
    "ingredientIds": [1, 2, 3]
  }'
```

Response:
```json
{
  "name": "Pancakes",
  "price": 12.50,
  "allergens": ["CEREALS_WITH_GLUTEN", "MILK", "EGGS"],
  "allergenWarning": "⚠ Contains: Cereals containing gluten, Eggs, Milk",
  "ingredients": [
    { "name": "Wheat flour", "allergens": ["CEREALS_WITH_GLUTEN"] },
    { "name": "Butter", "allergens": ["MILK"] },
    { "name": "Eggs", "allergens": ["EGGS"] }
  ]
}
```

---


## 🔐 Git Pull/Push Audit Hooks

This repo includes a transfer-audit script that can be wired into Git hooks to inspect changed files whenever you pull or push.

### Install hooks locally

```bash
git config core.hooksPath .githooks
```

### What gets audited

- Possible secrets/API keys/private keys
- Suspicious file names like `.env` and `id_rsa`
- Large files (default limit: 512KB)
- Binary-like artifacts (`.exe`, `.dll`, `.jar`, `.p12`, etc.)
- Potential hard-coded credentials in config files
- Destructive command patterns in `.sql` and `.sh` files (warning)

### Run manually

```bash
scripts/git-transfer-audit.sh --mode push --old <old-ref> --new <new-ref>
scripts/git-transfer-audit.sh --mode pull --old <old-ref> --new <new-ref>
```

## 🇪🇺 Supported Allergens (EU-14)

| Allergen | Examples |
|----------|----------|
| Celery | Stalks, leaves, seeds, celeriac |
| Cereals with gluten | Wheat, rye, barley, oats |
| Crustaceans | Crab, lobster, prawns, shrimp |
| Eggs | All egg-based products |
| Fish | All species |
| Lupin | Seeds and flour |
| Milk | All dairy, lactose |
| Molluscs | Mussels, oysters, squid |
| Mustard | Seeds, powder, oil |
| Tree nuts | Almonds, walnuts, cashews, etc. |
| Peanuts | Groundnuts, peanut oil |
| Sesame | Seeds, oil, tahini |
| Soybeans | Soya, edamame, tofu |
| Sulphur dioxide | Sulphites (>10mg/kg) |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 21** | Language |
| **Spring Boot 3.2** | Framework |
| **Spring Data JPA** | Data access with custom JPQL queries |
| **PostgreSQL** | Production database |
| **H2** | Development & testing database |
| **Flyway** | Database migrations |
| **Spring Cache** | Menu response caching |
| **SpringDoc OpenAPI** | Auto-generated Swagger documentation |
| **Lombok** | Boilerplate reduction |
| **JUnit 5 + MockMvc** | Integration testing |
| **Docker Compose** | One-command local stack |
| **GitHub Actions** | CI pipeline |

---

## 🧪 Running Tests

```bash
./mvnw clean verify
```

Tests include:
- Full CRUD lifecycle for all entities
- Allergen auto-computation verification
- Safe-dish filtering with exclusions
- Pagination validation
- Input validation and error handling
- 404 handling for missing resources

---

## 📋 Project Structure

```
src/main/java/com/safemenu/api/
├── config/          # OpenAPI configuration
├── controller/      # REST endpoints
├── dto/
│   ├── request/     # Input validation DTOs
│   └── response/    # Output DTOs
├── entity/          # JPA entities
├── enums/           # AllergenType enum (EU-14)
├── exception/       # Global error handling
├── mapper/          # Entity ↔ DTO mapping
├── repository/      # Spring Data repositories
└── service/         # Business logic layer
```

---

## 📄 License

MIT
