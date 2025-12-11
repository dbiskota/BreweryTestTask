package api;

import api.steps.BrewerySteps;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;
import static properties.TestData.GET_BREWERY_SEARCH;
import static properties.TestData.SEARCH_NAME;
import static utils.AssertUtil.assertCode;
import static utils.AssertUtil.assertCodeAndJson;

public class BreweryResourceTests {
    private final BrewerySteps brewerySteps = new BrewerySteps();

    @DataProvider(name = "breweriesNames")
    public Object[][] breweriesList(){
        return new Object[][]{{"Deft Brewing"}, {"Deft"}, {"DEFT BREWING"}, {"deft"}};
    }

    @DataProvider(name = "breweriesPerPage")
    public Object[][] breweriesPerPage(){
        return new Object[][]{{1}, {3}, {200}};
    }

    @DataProvider(name = "breweriesPages")
    public Object[][] breweriesPages(){
        return new Object[][]{{1, 2}};
    }

    //Check valid search for breweries names
    @Test (dataProvider = "breweriesNames")
    public void checkValidNameSearch(String breweryName){
        Response response = brewerySteps.searchBreweries(breweryName);
        assertCode(response, 200);

        List<String> names = response.jsonPath().getList("name");
        assertFalse(names.isEmpty(), "Empty list");

        boolean isFound = names.stream().anyMatch(n -> n.toLowerCase().contains(breweryName.toLowerCase()));
        assertTrue(isFound, "List haven`t " + breweryName + " name");
    }

    //Check per page limit parameter
    @Test (dataProvider = "breweriesPerPage")
    public void checkPageLimit(Integer perPage){
        Response response = brewerySteps.searchBreweries(SEARCH_NAME, perPage, null);
        assertCode(response, 200);

        List<Map<String, Object>> breweries = response.jsonPath().getList("$");
        long actualCount = breweries.stream().count();
        assertEquals((int)actualCount, perPage);
    }

    //Check that searching for a non-existent word returns an empty array
    @Test
    public void checkGibberishName() {
        String gibberish = "supercalifragilistic_no_beer_here_12345";

        Response response = brewerySteps.searchBreweries(gibberish);
        assertCode(response, 200);

        List<Map<String, Object>> results = response.jsonPath().getList("$");
        assertTrue(results.isEmpty());
    }

    //Check json schema validation
    @Test
    public void checkValidJson(){
        String breweryName = "Deft Brewing";
        Response response = brewerySteps.searchBreweries(breweryName);
        assertCodeAndJson(response, GET_BREWERY_SEARCH,200);
    }

    //Check that search on the different pages have different results
    @Test (dataProvider = "breweriesPages")
    public void checkPaginationPage(Integer page1, Integer page2){
        Response response = brewerySteps.searchBreweries(SEARCH_NAME, 3, page1);
        List<Map<String, Object>> breweries = response.jsonPath().getList("$");

        Response response1 = brewerySteps.searchBreweries(SEARCH_NAME, 3, page2);
        List<Map<String, Object>> breweries1 = response1.jsonPath().getList("$");

        boolean hasDuplicates = breweries.stream().anyMatch(breweries1::contains);
        assertFalse(hasDuplicates);
    }
}
