package com.code.solarwatch;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SolarWatchApplicationTests {


    @Autowired
    private MockMvc mockMvc;

    private static MockWebServer server;


    @BeforeAll
    public static void setup() throws IOException {
        server = new MockWebServer();
        server.start();
        System.setProperty("sunservice.Open-weather-api",
                server.url("/openweatherapi/").toString());
        System.setProperty("sunservice.Sunrise-sunset-api",
                server.url("/sunrisesunsetapi/").toString());
        System.setProperty("sunservice.Open-weather-api-key",
                server.url("testkey").toString());

    }

    @AfterAll
    public static void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void contextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    public void bpSunSetAndSunriseToday() throws Exception {
        RequestBuilder request =
                MockMvcRequestBuilders.get("/bpsunsetToday");

        server.enqueue(new MockResponse().setResponseCode(200)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"results\":{\"sunrise\":\"5:00:00 AM\",\"sunset\":\"5:00:00 PM\"}}"));


        String expectedResponse = "{\"sunrise\":\"5:00:00 AM\",\"sunset\":\"5:00:00 PM\"}";

        String responseString = mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();


        assertEquals(expectedResponse, responseString);


    }

    @Test
    public void testCitySunsetAndSunriseByDate() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/city2/2024-04-06/TestCity");

        String expectedResponse = "{\"sunrise\":\"7:00:00 AM\",\"sunset\":\"10:00:00 PM\"}";

        String responseString = mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedResponse, responseString);

    }


    @Test
    public void testNotValidCoordinates() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/locationanddate")
                .param("date", "2024-04-04")
                .param("lat", "100")
                .param("lon", "200");

        String response = mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("Not valid coordinate", response);

    }

}
