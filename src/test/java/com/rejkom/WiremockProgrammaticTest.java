package com.rejkom;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
Programmatic approach - allows to run any number of instances
WireMock will start before first test and close after the last test method
(because of 'static' filed - if not static then start and stop for each test method)
 */
public class WiremockProgrammaticTest {

    private static final String BASE_URI = "http://localhost:8090";

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8090).enableBrowserProxying(true)).build();

    @Test
    void getRequestTest() {
        //arrange
        wiremock.stubFor(get(urlEqualTo("/movie")).willReturn(ok()));

        //act
        Response response = given().baseUri(BASE_URI).get("/movie").thenReturn();

        //assert
        assertEquals(200, response.statusCode());
    }

    @Test
    void databaseQueryTest() {
        //arrange
        wiremock.stubFor(get("/database/query")
                .withRequestBody(equalTo("SELECT * FROM users WHERE id=1"))
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/plain")
                        .withBody("id: 1, username: user1, email: user1@example.com")));

        //act
        String response = given().baseUri(BASE_URI).body("SELECT * FROM users WHERE id=1").get("/database/query").getBody().asPrettyString();

        //assert
        System.out.println("Response: " + response);
        assertTrue(response.contains("user1"));
    }

    @Test
    void recordingFromCodeTest() {
        //start recording
        String targetBaseUrl = "http://real-api-server.com";
        wiremock.getRuntimeInfo().getWireMock().startStubRecording(targetBaseUrl);

        //operations performed on the target url
        //...

        //stop recording
        wiremock.getRuntimeInfo().getWireMock().stopStubRecording();

        //save the recorded stubs to files
        wiremock.getRuntimeInfo().getWireMock().takeSnapshotRecording();
    }


}
