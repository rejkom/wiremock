package com.rejkom;

import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;

class WiremockApplicationTest {

    private static final String WIREMOCK_SERVER = "http://localhost:8080/";

    @Test
    void shouldNotGetAccessToSesame() {
        //ValidatableResponse object can be used for assertions instead of JUnit assertions
        ValidatableResponse response = given().baseUri(WIREMOCK_SERVER).get("open/sesame").then().log().all();
        //Missing base auth header - should not get sesame open
        response.body(matchesPattern(".*I will not open the sesame.*"));
    }

    @Test
    void shouldGetAccessToSesame() {
        String response = given().baseUri(WIREMOCK_SERVER)
                .header(new Header("Authorization", "Basic dXNlcjpwYXNz")) //user and password in base64 format
                .get("open/sesame").asString();
        System.out.println(response);
        assertTrue(response.matches(".*The gold is yours.*"));
    }

}