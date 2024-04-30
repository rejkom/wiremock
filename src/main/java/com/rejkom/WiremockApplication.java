package com.rejkom;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WiremockApplication {

    public static void main(String[] args) {

        /* https://wiremock.org/docs/

        1. Standalone server: docker run -it --rm -p 8080:8080 --name wiremock_server wiremock/wiremock:3.5.4

        2. Check all mappings: http://localhost:8080/__admin/mappings

        3. Recorder: http://localhost:8080/__admin/recorder/
           Sample url for recorder: http://examples.wiremockapi.cloud
           Make GET request for: http://localhost:8080/recordables/123 and stop the recorder via url -
           the stub will be saved under resources/mappings (create the mappings folder if it doesn't exist)
           Then there if you run again a WireMock server it will replay using recorded json files -
           a connection with a service is not needed.

        4. Running on https (keystore can be provided or 'unsecured' message if not):
           WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                                          .httpsPort(443).keystorePath("").keystorePassword("");
           WireMockServer wireMockServer = new WireMockServer(config);
         */

        WireMockServer wireMockServer = new WireMockServer();
        wireMockServer.start();

        //'configureFor()' needed when we don't use a default server configuration like http://localhost:8080
        // to let know to which server stubs relate. If default then can be skipped
        configureFor("localhost", 8080);

        //GET stub example
        stubFor(get("/greetings").willReturn(aResponse().withBody("Hello there :)")));

        //Error resource example
        stubFor(get(("/error")).willReturn(serverError().withBody("Internal Server Error occurred")));

        //Open sesame stub and authorization handler
        stubFor(get("/open/sesame")
                .withHeader("Authorization", absent())
                .willReturn(unauthorized().withBody("I will not open the sesame!!!")));
        stubFor(get("/open/sesame")
                .withBasicAuth("user", "pass")
                .willReturn(aResponse().withBody("The gold is yours!!")));

        //Greetings: POST with expected body
        stubFor(post(urlMatching("/greetings/[0-9]+"))
                .atPriority(1) //can set a priority if multiple stubs matching
                .withRequestBody(containing("hello")) //request has to contain 'hello' in a request
                .willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json") //response
                        .withBody("{\"message\" : \"Thanks for greetings!\"}")));

        stubFor(post("/greetings/[0-9]+") //answer for above but when body doesn't contain 'hello'
                .willReturn(aResponse().withStatus(400).withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Request doesn't contain 'hello' greetings\"}")));

        //Application: POST with expected multiple bodies
        stubFor(post(urlEqualTo("/application")).withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(containing("\"name\": \"WireMock\""))
                .withRequestBody(containing("\"id\": \"12345\""))
                .willReturn(ok()));


        //Return xml body from a file
        stubFor(get(urlMatching("/users/123")).willReturn(aResponse()
                .withHeader("Content-Type", "application/xml").withBodyFile("response-body.xml")));

    }


}