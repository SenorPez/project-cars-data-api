package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.projectcars.EventControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
public class EventControllerTest_Single {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Test
    public void GetAllEvents_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEvent_InvalidId_ValidAcceptHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}", badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEvent_InvalidId_FallbackHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}", badId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEvent_InvalidId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/8675309").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventAllCars_InvalidId_ValidAcceptHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars", badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));

    }

    @Test
    public void GetSingleEventAllCars_InvalidId_FallbackHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars", badId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventAllCars_InvalidId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/8675309/cars").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCar_InvalidEventId_InvalidCarId_ValidAcceptHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", badEventId, badCarId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCar_InvalidEventId_InvalidCarId_FallbackHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", badEventId, badCarId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCar_InvalidEventId_InvalidCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/86753029/cars/1").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarClass_InvalidEventId_InvalidCarId_ValidAcceptHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", badEventId, badCarId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarClass_InvalidEventId_InvalidCarId_FallbackHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", badEventId, badCarId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarClass_InvalidEventId_InvalidCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/86753029/cars/1/class").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_InvalidEventId_InvalidCarId_ValidAcceptHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", badEventId, badCarId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_InvalidEventId_InvalidCarId_FallbackHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", badEventId, badCarId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_InvalidEventId_InvalidCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/86753029/cars/1/liveries").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_InvalidEventId_InvalidCarId_XXXLiveryId_ValidAcceptHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", badEventId, badCarId, inconsequentialLiveryId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_InvalidEventId_InvalidCarId_XXXLiveryId_FallbackHeader() throws Exception {
        Integer badEventId = 8675309;
        Integer badCarId = 1;
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", badEventId, badCarId, inconsequentialLiveryId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badEventId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badEventId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_InvalidEventId_InvalidCarId_XXXLiveryId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/86753029/cars/1/liveries/8675309").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
