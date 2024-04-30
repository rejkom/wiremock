package com.rejkom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Declarative approach - WireMock will start before first test and close after the last test method
Stub mappings and requests are reset before each test method.

1. @WireMockTest - default host and port: http://localhost:8080
2. @WireMockTest(httpPort = 8998) - custom port
3. @WireMockTest(httpsEnabled = true) - random port with https, e.g. https://localhost:2454
4. @WireMockTest(httpsEnabled = true, httpsPort = 8443) - https://localhost:8443
 */
@WireMockTest(httpPort = 8080)
public class WiremockDeclarativeTest {

    @Test
    void getRequestTest() {
        //arrange
        stubFor(get(urlEqualTo("/book")).willReturn(aResponse().withBody("Animal Farm")));

        //act
        Response response = given().baseUri("http://localhost").port(8080).get("/book").thenReturn();

        //assert
        assertTrue(response.getBody().asPrettyString().contains("Animal"));
    }

    @Test
    void getRequestWithRuntimeInfoTest(WireMockRuntimeInfo runtimeInfo) throws JsonProcessingException {
        //arrange
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expectedJson = mapper.readTree("{\"key\": \"value\"}");
        System.out.println("Running on host: " + runtimeInfo.getHttpBaseUrl());
        stubFor(post(urlEqualTo("/json-endpoint")).withRequestBody(equalToJson(expectedJson.toString()))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"JSON matched successfully\"}")));

        //act
        String response = given().baseUri("http://localhost").port(8080)
                .body("{\"key\": \"value\"}").post("/json-endpoint").thenReturn().getBody().print();

        //assert
        System.out.println("Response body: " + response);
        assertTrue(response.contains("successfully"));
    }

}
