package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class ApiService {
    private static String BASE_URL;
    private static boolean IS_CONSOLE_LOG;

    public static void setHttpRequestConfiguration(String baseUrl, boolean isConsoleLogUsed) {
        BASE_URL = baseUrl;
        IS_CONSOLE_LOG = isConsoleLogUsed;
    }
    public static Response getRequest(String endpoint, Map<String, Object> headers, Object body, Map<String, Object> queryParams, Map<String, String> pathParams) {
        RestAssured.reset();
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.log().uri();

        if (headers != null) for (Map.Entry<String, Object> header : headers.entrySet()) {
            requestSpecification.header(header.getKey(), header.getValue());
        }
        if (pathParams != null) for (Map.Entry<String, String> pathParam : pathParams.entrySet()) {
            requestSpecification.pathParam(pathParam.getKey(), pathParam.getValue());
        }
        if (queryParams != null) for (Map.Entry<String, Object> queryParam : queryParams.entrySet()) {
            requestSpecification.queryParam(queryParam.getKey(), queryParam.getValue());
        }

        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);

        if (body != null) {
            requestSpecification.body(body);
        }

        if (IS_CONSOLE_LOG) {
            requestSpecification.then().log().all();
        }

        return requestSpecification.get(BASE_URL + endpoint);
    }
}
