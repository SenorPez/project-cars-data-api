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
public class EventControllerTest_Parameterized_EventsCarsLiveries {
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
    @Parameter(value = 2)
    public Integer carId;
    @Parameter(value = 3)
    public Car resultCar;
    @Parameter(value = 4)
    public Integer liveryId;
    @Parameter(value = 5)
    public Livery resultLivery;

    @Parameters(name = "eventId: {0}, carId: {2}, liveryId: {4}")
    public static Iterable<Object[]> parameters() {
        return Application.EVENTS.stream()
                .flatMap(event -> event.getCars().stream()
                        .flatMap(car -> car.getLiveries().stream()
                                .map(livery -> new Object[]{event.getId(), event, car.getId(), car, livery.getId(), livery})))
                .collect(Collectors.toList());
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_ValidCarId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(LIVERY_COLLECTION_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), resultCar.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("id", resultLivery.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("name", resultLivery.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        containsInAnyOrder(
                                                hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()),
                                                hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId())))))))
                .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries"),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries"))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:car", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_ValidCarId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(LIVERY_COLLECTION_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("id", resultLivery.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("name", resultLivery.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        containsInAnyOrder(
                                                hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()),
                                                hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId())))))))
                .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries"),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries"))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:car", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId()))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_ValidCarId_ValidLiveryId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(LIVERY_OBJECT_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), resultCar.getId(), resultLivery.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultLivery.getId())))
                .andExpect(jsonPath("$.name", is(resultLivery.getName())))
                .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:liveries", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries"),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries"))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_ValidCarId_ValidLiveryId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(LIVERY_OBJECT_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), resultCar.getId(), resultLivery.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultLivery.getId())))
                .andExpect(jsonPath("$.name", is(resultLivery.getName())))
                .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:liveries", containsInAnyOrder(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries"),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId() + "/liveries"))))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_ValidCarId_ValidLiveryId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), resultCar.getId(), resultLivery.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
