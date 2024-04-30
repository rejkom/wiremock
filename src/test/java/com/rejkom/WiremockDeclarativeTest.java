package com.rejkom;

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

    //TODO: json body

    @Test
    void getRequestWithRuntimeInfoTest(WireMockRuntimeInfo runtimeInfo) {
        System.out.println("Running on host: " + runtimeInfo.getHttpBaseUrl());
    }

}
