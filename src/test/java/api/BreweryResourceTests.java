package api;

import api.steps.BrewerySteps;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static utils.AssertUtil.assertCode;

public class BreweryResourceTests {
    private final BrewerySteps brewerySteps = new BrewerySteps();

    @DataProvider(name = "breweriesNames")
    public Object[][] breweriesList(){
        return new Object[][]{{"Deft Brewing"}, {"Deft"}, {"DEFT BREWING"}, {"deft"} };
    }

    @Test (dataProvider = "breweriesNames")
    public void checkValidNameSearch(String breweryName){
        Response response = brewerySteps.searchBreweries(breweryName);
        assertCode(response, 200);

        List<String> names = response.jsonPath().getList("name");

        assertFalse(names.isEmpty(), "Empty list");

        boolean isFound = names.stream().anyMatch(n -> n.toLowerCase().contains(breweryName.toLowerCase()));

        assertTrue(isFound, "List haven`t " + breweryName + " name");
    }


}
