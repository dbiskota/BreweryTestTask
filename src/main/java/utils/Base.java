package utils;

import org.aeonbits.owner.ConfigFactory;
import properties.TestInitValues;
import properties.TestProperties;

import static utils.ApiService.setHttpRequestConfiguration;

public class Base{
    public static TestProperties testProp = ConfigFactory.create(TestProperties.class);
    public static TestInitValues testCred = ConfigFactory.create(TestInitValues.class);


    static {
        setHttpRequestConfiguration(testCred.BASE_URL(), testProp.consoleLog());
    }
}
