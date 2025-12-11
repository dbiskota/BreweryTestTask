package properties;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:base.properties"})
public interface TestInitValues extends Config {

    @Key("BASE_URL")
    String BASE_URL();
}
