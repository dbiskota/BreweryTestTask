## Task 1: "Search Breweries" Automation
**Endpoint:** [`https://www.openbrewerydb.org/documentation#search-breweries`](https://www.openbrewerydb.org/documentation#search-breweries)

**Requirements:**
* **Stack:** Java + REST Assured.
* **Scope:** Select up to 5 scenarios covering the main features of the method and implement them in code.
* **Documentation:** List any remaining scenarios that should be included in a complete test suite in the README file.

---

## Additional test cases for "Search Breweries"

Below is a list of scenarios covered (or planned) by automated tests for the `GET /breweries/search` endpoint.

### Search Logic
-  Verify that the search operates only by `name` but not by `city` (e.g., query "San Diego").
- Verify that leading and trailing whitespace is automatically trimmed and does not affect the result.
- Search using emojis, using URL-encoded characters (`%20`, `&`, `?`), internationalization support (Cyrillic, etc.).

### Pagination & Limits
- Verify behavior when `per_page` exceeds the allowed maximum (e.g., 1000). Expect truncation to the default maximum (e.g., 50).
- Verify response to `per_page=0` or `-1` (should return default count or 400 Bad Request).
- Passing text into numeric parameters (e.g., `per_page=abc`) and floating-point numbers into integer parameters (e.g., `page=1.5`).

### Data Integrity & Contract
- Ensure critical fields (`id`, `name`) are never `null`.
- If `latitude` exists, `longitude` must also be present, value range validation (Lat: -90...90, Long: -180...180).
- Ensure no duplicate `id`s in the search result list.

### Edge Cases
- Handle extremely long query strings (e.g., 1000+ characters).
- Verify system behavior when passing an empty `query` parameter.

### Security
- Verify resilience against injection attempts (e.g., `' OR 1=1`).
- Verify input sanitization (e.g., attempting to pass `<script>`).
- Verify that `POST`, `PUT`, `DELETE` methods return `405 Method Not Allowed`.

### Performance
- Verify that latency does not exceed the defined threshold (e.g., < 500ms).

---

## Task 2: "List Breweries" Analysis
**Endpoint:** [`https://www.openbrewerydb.org/documentation#list-breweries`](https://www.openbrewerydb.org/documentation#list-breweries)

**Requirements:**
* Examine the method and document your thoughts in the README.
* **Analysis:** Describe how you would apply test automation to this method (approach, test design techniques, etc.).
* **Estimation:** Provide an estimated effort for completing the automation of this task.

---
