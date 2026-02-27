# SafeMenu Frontend Starter (React + Vite)

A minimal React app to explore the SafeMenu API in a browser UI.

## Features

- Load restaurants from `GET /api/v1/restaurants`
- Filter safe dishes from `GET /api/v1/dishes/safe`
- Toggle EU-14 allergens to build the `exclude` query dynamically

## Run locally

```bash
cd frontend-starter
npm install
npm run dev
```

Open: `http://localhost:5173`

> Keep the API running on `http://localhost:8080`.
>
> By default, the app now uses the Vite dev proxy (`/api` -> `http://localhost:8080`) to avoid browser CORS issues.

## Configure API URL

Copy `.env.example` to `.env` only if you want to override the default proxy behavior.

- Leave `VITE_API_BASE_URL` empty (recommended for local dev with Vite proxy).
- Set `VITE_API_BASE_URL` (for deployed API URLs).

```bash
cp .env.example .env
```
