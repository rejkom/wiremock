package com.rejkom;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
Programmatic approach - allows to run any number of instances
WireMock will start before first test and close after the last test method
(because of 'static' filed - if not static then start and stop for each test method)
 */
public class WiremockProgrammaticTest {

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8090).enableBrowserProxying(true)).build();

    @Test
    public void getRequestTest() {
        //arrange
        wiremock.stubFor(get(urlEqualTo("/movie")).willReturn(ok()));

        //act
        Response response = given().baseUri("http://localhost").port(8090).get("/movie").thenReturn();

        //assert
        assertEquals(200, response.statusCode());
    }

}
