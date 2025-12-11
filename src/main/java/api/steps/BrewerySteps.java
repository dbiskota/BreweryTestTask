package api.steps;

import io.restassured.response.Response;
import utils.BaseTest;

import java.util.HashMap;

import static api.endpoints.BreweryEndpoints.BREWERY_SEARCH;
import static properties.TestData.*;
import static utils.ApiService.getRequest;

public class BrewerySteps extends BaseTest {
    public Response searchBreweries(String breweryName) {

        HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put(QUERY, breweryName);

        return getRequest(BREWERY_SEARCH, null, null, queryParams, null);
    }

    public Response searchBreweries(String breweryName, Integer perPage, Integer page) {

        HashMap<String, Object> queryParams = new HashMap<>();
        queryParams.put(QUERY, breweryName);

        if (perPage != null) {
            queryParams.put(PER_PAGE, perPage);
        }

        if (page != null) {
                queryParams.put(PAGE, page);
        }

        return getRequest(BREWERY_SEARCH, null, null, queryParams, null);
    }
}
