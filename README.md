## Task 1: "Search Breweries" Automation
**URL document:** [https://www.openbrewerydb.org/documentation#search-breweries](https://www.openbrewerydb.org/documentation#search-breweries)

**Requirements:**
* **Stack:** Java + REST Assured.
* **Scope:** Select up to 5 scenarios covering the main features of the method and implement them in code.
* **Documentation:** List any remaining scenarios that should be included in a complete test suite in the README file.

## Link to documentation with bugs found:
[Returns 422 error when 'query' parameter consists of only 2 characters and returns results matching 'city' field instead of only 'name'](https://drive.google.com/file/d/1LVF9gbWCmr1B9pgR0aKtH0f9TMCXClIe/view?usp=sharing)

---

## Additional test cases for "Search Breweries"

Below is a list of scenarios covered by automated tests for the `GET /breweries/search` endpoint.

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


### 1. Analysis
This endpoint relies on strict filtering, sorting, and geolocation logic.

- There are multiple filtering parameters (`by_city`, `by_name`, `by_state`, `by_type`) that can be used individually or combined.
- The `by_dist` parameter introduces geospatial logic (latitude/longitude), requiring mathematical verification (e.g., Haversine formula) within the tests to ensure accuracy.
- Sorting logic (`sort=type,name:asc`) must be tested to ensure it respects the active filters.
- Conflict Rule: The by_dist parameter cannot be combined with the sort parameter.
- `by_state` requires the full state name (no abbreviations).
- Supports both 5-digit and 9-digit (postal+4) formats.


### 2. Test Automation Strategy
To achieve maximum coverage with minimal maintenance, I will apply the following strategies:

####  Data-Driven Testing 
1. Since there are 8 distinct filter parameters (by_city, by_name, by_state, by_postal, by_type, by_country, by_ids), writing individual tests is inefficient.
Instead of writing separate test methods for each filter (one for city, one for state, etc.), I will utilize a DataProvider.
Datasets:
- `by_city`: "San Diego", "New_York"
- `by_state`: "California" (Valid), "CA" (Invalid - specific negative test)
- `by_postal`: "92109" (5-digit), "92109-2802" (9-digit)

2. Iterate through the valid enum list (micro, nano, regional, brewpub, planning, contract, proprietor, closed) and ensure the response returns only breweries of that specific type.

#### Pairwise
 Test strategic combinations rather than all possibilities:
- `by_state` + `by_type` 
- `by_city` + `sort`
- `by_dist` + `per_page`

####  Boundary Values
Critical for verifying pagination limits and coordinate inputs.
- `per_page`: `0`, `1`, `50`, `200` (max), `201` (invalid).
- `by_dist`: `0,0`, Invalid coordinates (Lat > 90), Valid coordinates.

### 3. Test Scenarios Plan

#### Positive Scenarios (Functional)
1.  Verify the endpoint returns a list (default 20 items) when no parameters are provided.
2.  Single Filters:
- Verify all results have `city: San Diego`.
- Verify all results have `state: California`.
- Verify all results have `brewery_type: micro`, etc.
3.  Geospatial Sort (`by_dist`):
- Pass user coordinates to the endpoint.
- Calculate the distance between the user's origin and the result's lat/long in the test code. Assert that results are sorted by distance (nearest first).
4.  Sorting:
- Verify alphabetical order (`sort=name:asc`)
- Verify multi-level sorting logic (`sort=type,name:desc`)
5. Type Validation (`by_type`):
- Iterate through: micro, nano, regional, brewpub, planning, contract, proprietor, closed.
- Verify that brewery_type in response matches the request.

#### Negative Scenarios
- Should return an empty list or 422
- Should handle gracefully (400 status code).
- Should return an empty list.
- Documentation rules: by_dist=...&sort=name:asc (Does it error?).
- Invalid state format: by_state=CA (Abbreviation). Should returns empty list (since docs require full name).
- Pagination limits (p`per_page = 201`), should return error

### 4. Test Coverage Checklist

#### Filters
-  `by_city:` Filter by exact city name (test underscores vs URL encoding).
-  `by_name:` Filter by brewery name.
-  `by_state:`
    -  Valid full name (e.g., "California").
    - Invalid abbreviation (e.g., "CA") 
-  `by_postal:`
    -  5-digit code.
    -  9-digit code (postal+4).
-  `by_country:1 Filter by country.
-  `by_ids:` Filter by comma-separated list of UUIDs.
-  `by_type:` Verify all valid enums:
    -  `micro`, `nano`, `regional`, `brewpub`, `planning`, `contract`, `proprietor`, `closed`.
    -  Check behavior for deprecated types (`large`, `bar`).

#### Geospatial & Sorting
-  `by_dist:`
    - Verify results are sorted by distance from origin. 
    - Verify conflict when sent with `sort` parameter.
-  `sort:`
    -  Single field (`name:asc`).
    -  Multi-field (`type,name:desc`).

#### Pagination
- `page:` Verify offset (Page 1 data != Page 2 data).
- `per_page:`
    -  Standard values (10, 50).
    -  Max limit (200).
    - Exceeding max (201)

#### Contract & Data Integrity
- Validate types (String vs Null) for `address_1`, `address_2`, `latitude`, `longitude`.
-   Ensure `id`, `name`, `brewery_type` are never null.

### 5. Estimation

Given that the framework core (Base, Utils, POJO models) is already established, the estimation focuses on script creation and logic implementation.

| Task                                | Complexity | Estimated Time |
|:------------------------------------| :--- |:---------------|
| Basic Filters                       | Low | 1 hour         |
| Pagination & Sorting Logic          | Medium | 1 hour         |
| Geospatial (`by_dist`) Verification | High | 2 hours        |
| Negative & Edge Cases               | Medium | 1 hour         |
| Refactoring & Documentation         | Low | 1 hours        |
| Total                               | | **~6 hours**   |
