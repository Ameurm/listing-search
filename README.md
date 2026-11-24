# ListingSearch

A full-stack property listing search assessment implementation using Java 21, Spring Boot, Maven, React 19, TypeScript, and Vite.

## Project structure

```text
ListingSearch/
├── backend/ListingSearch/       # Spring Boot REST API
├── frontend/                    # React + TypeScript client
├── data/sample_listings.json    # Dataset used by the API
└── README.md
```

## Requirements

- JDK 21
- Node.js 20.19+ or 22.12+
- Internet access on the first Maven/npm build to download dependencies

The Maven wrapper is included, so a global Maven installation is not required.

## Run the backend (PowerShell)

From the repository root:

```powershell
Set-Location .\backend\ListingSearch
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

API: `http://localhost:8080/api/listings`

## Run the frontend (second terminal)

From the repository root:

```powershell
Set-Location .\frontend
npm install
Copy-Item .env.example .env
npm run dev
```

UI: `http://localhost:5173`

The frontend reads `VITE_API_BASE_URL` from `.env`; if it is not set, it defaults to `http://localhost:8080`.

## API examples

```http
GET /api/listings
GET /api/listings?minPrice=400000&maxPrice=550000&minBedrooms=3&city=Springfield
GET /api/listings?keyword=pet&targetBudget=500000&page=0&size=5
```

Supported query parameters:

| Parameter | Meaning | Validation |
|---|---|---|
| `minPrice` | Minimum asking price | Must be >= 0 |
| `maxPrice` | Maximum asking price | Must be >= 0 |
| `minBedrooms` | Minimum bedrooms | Must be >= 0 |
| `city` | Case-insensitive exact city filter | Optional |
| `keyword` | Case-insensitive substring search in address, city, state, ZIP, and description | Optional |
| `targetBudget` | Budget used by relevance ranking | Must be >= 0 |
| `page` | Zero-based page index | Must be >= 0; default 0 |
| `size` | Page size | 1..100; default 10 |

`minPrice` cannot exceed `maxPrice`; invalid requests return HTTP 400 with a Problem Details response.

## Ranking approach

Results are sorted by a transparent relevance score, descending. When a positive target budget is supplied:

- Budget score = `max(0, 1 - abs(listingPrice - targetBudget) / targetBudget)`
- Recency score = `exp(-ageInDays / 60)`
- Combined score = `0.60 * budgetScore + 0.40 * recencyScore`

When no positive target budget is supplied, recency score is used on its own. The weights and 60-day decay are explicit implementation choices because the assessment brief did not prescribe a formula. Ties are resolved by newest listing date, then source and ID for stable ordering. Future listing dates are treated as age zero for recency scoring.

## Dataset note

The included sample data is a reconstructed, self-contained dataset. It preserves the previously verified record counts and known city/price values (12 listings; 11 ACTIVE and 1 PENDING; Springfield 4, Fairfax 2, Reston 2, Vienna 2, Manassas 1, Chantilly 1). The original uploaded `sample_listings.json` was not available during this rebuild, so replace this file with the assessment's original dataset if exact source records are required.

## Identity and duplicate records

A listing is identified by the composite key `(source, id)` because different MLS sources can contain overlapping IDs or duplicate property representations. The API returns the original source records rather than merging them.
