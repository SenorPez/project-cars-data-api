package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.projectcars.EventControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Parameterized.class)
@SpringBootTest
public class EventControllerTest_Parameterized_Events {
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

    @Parameter
    public Integer eventId;
    @Parameter(value = 1)
    public Event resultEvent;

    @Parameters(name = "eventId: {0}")
    public static Iterable<Object[]> parameters() {
        return Application.EVENTS.stream()
                .limit(10)
                .map(event -> new Object[]{event.getId(), event})
                .collect(Collectors.toList());
    }

    @Test
    public void GetAllEvents_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

        mockMvc.perform(get("/events").accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:event", hasItem(hasEntry("id", resultEvent.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:event", hasItem(hasEntry("name", resultEvent.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:event", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        hasEntry("href", "http://localhost/events/" + resultEvent.getId()))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetAllEvents_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

        mockMvc.perform(get("/events").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:event", hasItem(hasEntry("id", resultEvent.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:event", hasItem(hasEntry("name", resultEvent.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:event", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        hasEntry("href", "http://localhost/events/" + resultEvent.getId()))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEvent_ValidId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

        mockMvc.perform(get("/events/{eventId}", resultEvent.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultEvent.getId())))
                .andExpect(jsonPath("$.name", is(resultEvent.getName())))
                .andExpect(jsonPath("$.tier", is(resultEvent.getTier())))
                .andExpect(jsonPath("$.verified", is(resultEvent.getVerified())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.pcars:events", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.pcars:rounds", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEvent_ValidId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

        mockMvc.perform(get("/events/{eventId}", resultEvent.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultEvent.getId())))
                .andExpect(jsonPath("$.name", is(resultEvent.getName())))
                .andExpect(jsonPath("$.tier", is(resultEvent.getTier())))
                .andExpect(jsonPath("$.verified", is(resultEvent.getVerified())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.pcars:events", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.pcars:rounds", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEvent_ValidId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}", resultEvent.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCar_ValidEventId_InvalidCarId_ValidAcceptHeader() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCar_ValidEventId_InvalidCarId_FallbackHeader() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), badId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCar_ValidEventId_InvalidCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/8675309", resultEvent.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarClass_ValidEventId_InvalidCarId_ValidAcceptHeader() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", resultEvent.getId(), badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarClass_ValidEventId_InvalidCarId_FallbackHeader() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", resultEvent.getId(), badId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarClass_ValidEventId_InvalidCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/8675309/class", resultEvent.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_InvalidCarId_ValidAcceptHeader() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_InvalidCarId_FallbackHeader() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), badId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_InvalidCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/8675309/liveries", resultEvent.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_InvalidCarId_XXXLiveryId_ValidAcceptHeader() throws Exception {
        Integer badId = 1;
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), badId, inconsequentialLiveryId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_InvalidCarId_XXXLiveryId_FallbackHeader() throws Exception {
        Integer badId = 1;
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), badId, inconsequentialLiveryId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_InvalidCarId_XXXLiveryId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/1/liveries/8675309", resultEvent.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
