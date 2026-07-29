[README (1).md](https://github.com/user-attachments/files/30490843/README.1.md)
# GOT-map — Fantasy World Backend

A Google-Maps-style backend for a fictional world, built with Spring Boot and PostgreSQL. Originally scaffolded and tested using Game of Thrones locations as placeholder data, with the intent of eventually holding the geography of an original book.

This document explains what was built, why it was built that way, and what's left — so you can either maintain this version or rebuild your own from the same blueprint.

---

## 1. The Core Idea

Real map backends (Google Maps, Apple Maps) boil down to a few components:

1. **A data model** — places, with coordinates and metadata
2. **A search/lookup API** — find things by name
3. **A routing engine** — shortest path between two points
4. **Tile serving** — the visual map image, sliced for zoom levels
5. **Layers** — toggleable overlays (political borders, terrain, etc.)

This project builds 1–3 fully. Tiles and layers are deferred until there's a real map image to work from.

---

## 2. Tech Stack

| Layer | Choice | Why |
|---|---|---|
| Language | Java | Matches existing skill track and job-search stack |
| Framework | Spring Boot 4.1.0 | Current stable version; modularized starters (`spring-boot-starter-webmvc` replaced the old `spring-boot-starter-web`) |
| Database | PostgreSQL | Industry-standard relational DB; real geospatial systems (PostGIS) build on it |
| ORM | Spring Data JPA / Hibernate | Auto-generates SQL from Java entity classes |
| Build tool | Maven | Default for Spring Initializr projects |

---

## 3. Architecture — Layered Design

```
Client (curl / Postman / future frontend)
        │
        ▼
   Controller layer     → handles HTTP requests, no business logic
        │
        ▼
   Service layer        → business logic (routing algorithm lives here)
        │
        ▼
   Repository layer     → talks to the database (Spring Data JPA)
        │
        ▼
   PostgreSQL            → stores everything
```

**Why split it up this way:** when something breaks, you know where to look. A bug in "how distance is calculated" is in `service/`. A bug in "the API returns the wrong JSON shape" is in `controller/`. This is the standard pattern for backend projects of any real size.

Package structure:
```
com.map.city
├── entity/       → what the data looks like
├── repository/   → database access (mostly one-liner interfaces)
├── service/      → business logic (RoutingService)
├── controller/   → REST endpoints
└── exception/    → global error handling
```

---

## 4. The Data Model

Four entities, all connected:

### `City`
A named point with coordinates and population.
```
id, name, x, y, population, region (→ Region)
```

### `Region`
A grouping/political boundary. Self-referencing, so regions can nest (e.g., a duchy inside a kingdom).
```
id, name, description, parentRegion (→ Region, nullable)
```

### `PointOfInterest`
Non-city landmarks — temples, ruins, battlefields, anything that isn't a settlement but matters on the map.
```
id, name, type, x, y, region (→ Region)
```

### `Road`
An edge connecting two cities, carrying a real-world distance and a terrain label.
```
id, fromCity (→ City), toCity (→ City), distance, terrain
```

**Key design decision — coordinates vs. real distance are decoupled.** `x`/`y` are for visual placement on a map image later. `Road.distance` is a real number (we used actual researched mileage — e.g., the Kingsroad is ~1,460 miles) entered by hand, *not* calculated from x/y. This matters because real roads wind around mountains and rivers — straight-line pixel distance would never match true travel distance. This also sidesteps a problem every fan-made GoT map runs into: the "official" scale is inconsistent (the Wall comes out to ~240 miles in most fan geo-data instead of the canon 300).

---

## 5. API Endpoints

All entities follow the same CRUD shape:

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/cities` | List all |
| GET | `/api/cities/{id}` | Get one |
| POST | `/api/cities` | Create |
| PUT | `/api/cities/{id}` | Update (replaces all fields) |
| DELETE | `/api/cities/{id}` | Delete |

Same pattern for `/api/regions`, `/api/points-of-interest`, `/api/roads`.

**Search:**
| Endpoint | Purpose |
|---|---|
| `/api/search?q=X` | Search across cities, regions, and POIs at once |
| `/api/search/cities?q=X` | Cities only |
| `/api/search/regions?q=X` | Regions only |
| `/api/search/points-of-interest?q=X` | POIs only |

**Routing:**
| Endpoint | Purpose |
|---|---|
| `/api/routes/straight-line?from=X&to=Y` | Raw geometric distance between two cities (ignores roads) |
| `/api/routes/shortest?from=X&to=Y` | Real shortest path via roads — returns the path, total distance, and estimated travel time |

---

## 6. The Routing Algorithm

This is the most technically interesting part of the project.

**Straight-line distance** is trivial — basic Euclidean distance (`√((x₂-x₁)² + (y₂-y₁)²)`) using coordinates directly.

**Shortest path** uses **Dijkstra's algorithm**:
1. Build a graph in memory from every `Road` row — each road becomes a two-way edge (city A ↔ city B) weighted by its `distance`.
2. Run standard Dijkstra from the origin city, using a priority queue that always expands the currently-closest unvisited city.
3. When the destination is reached, walk backward through a `previous` map to reconstruct the actual path (not just the total distance — this is the part that's easy to skip if you've only done "shortest distance" versions of Dijkstra on LeetCode).
4. Once the path is known, walk it *forward* again, look up the terrain of each road segment, and sum up travel days using a per-terrain speed table (e.g., a well-built "kingsroad" moves faster than plain "road", which moves faster than "forest" or "mountains").

Example result for Winterfell → Storm's End (no direct road, routed through King's Landing):
```json
{"cityIdPath":[1,4,5],"totalDistance":1845.0,"totalTravelDays":64.7}
```

---

## 7. Key Engineering Decisions (and why)

- **Constructor injection**, not `@Autowired` field injection — the modern recommended Spring pattern, easier to test.
- **`ddl-auto=update`** — Hibernate auto-creates/alters tables from entity classes. Fine for solo development; would switch to migration tools (Flyway/Liquibase) for a team/production setting.
- **Bean validation (`@NotBlank`, `@Positive`) + a global `@RestControllerAdvice`** — bad input (blank names, negative population) is rejected with a clean 400 response instead of crashing with a raw stack trace. A separate handler catches `DataIntegrityViolationException` (e.g., trying to delete a region that's still referenced by a city) and returns a clean 409 instead of a generic 500.
- **PUT replaces the whole entity.** This is standard REST semantics, but it has a real gotcha: if you PUT a city without including its `region` field, the link gets wiped to `null`. Worth knowing before you build a frontend that does partial updates — you'd want PATCH semantics for that instead.
- **`data.sql`** was used for one-time bulk seeding, then intentionally emptied — Spring re-runs it on *every* restart, so leaving real INSERT statements in it permanently would cause duplicate-key crashes on the next restart.

---

## 8. What's Deliberately Not Built Yet

- **Tile serving** — needs an actual map image to slice into zoom levels. No point building this against placeholder data.
- **Layers** (political borders, terrain overlays) — depends on richer region/geometry data than currently exists.
- **Tests** — no Mockito unit tests yet (an earlier practice project, `task-manager`, has this pattern already if you want a template).
- **DTOs** — entities are currently returned directly as API responses. Fine at this scale; a larger project would separate "database shape" from "API response shape."

---

## 9. How to Build Your Own Version

If you're rebuilding this from scratch as a learning exercise, the recommended order is:

1. Scaffold a Spring Boot project (Spring Web/WebMVC, Spring Data JPA, PostgreSQL Driver, Validation)
2. Set up Postgres locally, connect via `application.properties`
3. Build **one** entity fully (entity → repository → controller → test with curl) before touching the others — get the full loop working end to end first
4. Repeat for the remaining entities
5. Add relationships between entities (`@ManyToOne`) once each entity works independently
6. Add the routing/business logic layer once the data model is stable
7. Add validation and error handling last, once you know what kinds of bad input are actually possible

The biggest lesson from building this version: **get one thing fully working before scaffolding everything else.** Building all four controllers empty at once (which happened partway through this project) created confusion about what was actually done versus just stubbed out.
