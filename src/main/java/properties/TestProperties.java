package properties;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:test.properties")
public interface TestProperties extends Config {

    @Key("consoleLog")
    Boolean consoleLog();

}