package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventControllerTest {
    private MockMvc mockMvc;
    private static final MediaType contentType = new MediaType("application", "vnd.senorpez.pcars.v1+json", StandardCharsets.UTF_8);
    private static final ClassLoader classLoader = EventControllerTest.class.getClassLoader();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void TestGetAllEvents() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("events.schema.json");

        mockMvc.perform(get("/events").header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:event", hasSize(123)))
                // TODO: 6/4/2017 Determine how to test each items "_links" property to make sure they're formed properly.
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllEvents_JsonFallback() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("events.schema.json");

        mockMvc.perform(get("/events").header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:event", hasSize(123)))
                // TODO: 6/4/2017 Determine how to test each items "_links" property to make sure they're formed properly.
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllEvents_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleEvent_Exists() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("event.schema.json");

        mockMvc.perform(get("/events/{eventId}", resultEvent.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultEvent.getId())))
                .andExpect(jsonPath("$.name", is(resultEvent.getName())))
                .andExpect(jsonPath("$.tier", is(resultEvent.getTier())))
                .andExpect(jsonPath("$.verified", is(resultEvent.isVerified())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:events", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.pcars:rounds", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleEvent_Exists_JsonFallback() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("event.schema.json");

        mockMvc.perform(get("/events/{eventId}", resultEvent.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultEvent.getId())))
                .andExpect(jsonPath("$.name", is(resultEvent.getName())))
                .andExpect(jsonPath("$.tier", is(resultEvent.getTier())))
                .andExpect(jsonPath("$.verified", is(resultEvent.isVerified())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:events", hasEntry("href", "http://localhost/events")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.pcars:rounds", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleEvent_Exists_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}", resultEvent.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleEvent_DoesNotExist() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}", badId).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));
    }

    @Test
    public void TestGetSingleEvent_DoesNotExist_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/8675309").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetAllEventCars() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("eventcars.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars", resultEvent.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:event", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllEventCars_JsonFallback() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("eventcars.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars", resultEvent.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:event", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllEventCars_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars", resultEvent.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleEventCar_Exists() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Car resultCar = resultEvent.getCars().stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("eventcar.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultCar.getId())))
                .andExpect(jsonPath("$.manufacturer", is(resultCar.getManufacturer())))
                .andExpect(jsonPath("$.model", is(resultCar.getModel())))
                .andExpect(jsonPath("$.country", is(resultCar.getCountry())))
                .andExpect(jsonPath("$.year", is(resultCar.getYear())))
                .andExpect(jsonPath("$.drivetrain", is(resultCar.getDrivetrain().toString())))
                .andExpect(jsonPath("$.enginePosition", is(resultCar.getEnginePosition().getDisplayString())))
                .andExpect(jsonPath("$.engineType", is(resultCar.getEngineType())))
                .andExpect(jsonPath("$.topSpeed", is(resultCar.getTopSpeed())))
                .andExpect(jsonPath("$.horsepower", is(resultCar.getHorsepower())))
                .andExpect(jsonPath("$.acceleration", closeTo(Double.valueOf(resultCar.getAcceleration()), 0.001)))
                .andExpect(jsonPath("$.braking", closeTo(Double.valueOf(resultCar.getBraking()), 0.001)))
                .andExpect(jsonPath("$.weight", is(resultCar.getWeight())))
                .andExpect(jsonPath("$.torque", is(resultCar.getTorque())))
                .andExpect(jsonPath("$.weightBalance", is(resultCar.getWeightBalance())))
                .andExpect(jsonPath("$.wheelbase", closeTo(Double.valueOf(resultCar.getWheelbase()), 0.001)))
                .andExpect(jsonPath("$.shiftPattern", is(resultCar.getShiftPattern().getDisplayString())))
                .andExpect(jsonPath("$.shifter", is(resultCar.getShifter().getDisplayString())))
                .andExpect(jsonPath("$.gears", is(resultCar.getGears())))
                .andExpect(jsonPath("$.dlc", is(resultCar.getDlc())))
                .andExpect(jsonPath("$._links.self", contains(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId()))
                ))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.pcars:class", hasEntry("href", "http://localhost/classes/" + resultCar.getCarClass().getId())))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleEventCar_Exists_JsonFallback() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Car resultCar = resultEvent.getCars().stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("eventcar.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultCar.getId())))
                .andExpect(jsonPath("$.manufacturer", is(resultCar.getManufacturer())))
                .andExpect(jsonPath("$.model", is(resultCar.getModel())))
                .andExpect(jsonPath("$.country", is(resultCar.getCountry())))
                .andExpect(jsonPath("$.year", is(resultCar.getYear())))
                .andExpect(jsonPath("$.drivetrain", is(resultCar.getDrivetrain().toString())))
                .andExpect(jsonPath("$.enginePosition", is(resultCar.getEnginePosition().getDisplayString())))
                .andExpect(jsonPath("$.engineType", is(resultCar.getEngineType())))
                .andExpect(jsonPath("$.topSpeed", is(resultCar.getTopSpeed())))
                .andExpect(jsonPath("$.horsepower", is(resultCar.getHorsepower())))
                .andExpect(jsonPath("$.acceleration", closeTo(Double.valueOf(resultCar.getAcceleration()), 0.001)))
                .andExpect(jsonPath("$.braking", closeTo(Double.valueOf(resultCar.getBraking()), 0.001)))
                .andExpect(jsonPath("$.weight", is(resultCar.getWeight())))
                .andExpect(jsonPath("$.torque", is(resultCar.getTorque())))
                .andExpect(jsonPath("$.weightBalance", is(resultCar.getWeightBalance())))
                .andExpect(jsonPath("$.wheelbase", closeTo(Double.valueOf(resultCar.getWheelbase()), 0.001)))
                .andExpect(jsonPath("$.shiftPattern", is(resultCar.getShiftPattern().getDisplayString())))
                .andExpect(jsonPath("$.shifter", is(resultCar.getShifter().getDisplayString())))
                .andExpect(jsonPath("$.gears", is(resultCar.getGears())))
                .andExpect(jsonPath("$.dlc", is(resultCar.getDlc())))
                .andExpect(jsonPath("$._links.self", contains(
                        hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId()))
                ))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                .andExpect(jsonPath("$._links.pcars:class", hasEntry("href", "http://localhost/classes/" + resultCar.getCarClass().getId())))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleEventCar_Exists_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Car resultCar = Application.CARS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleEventCar_DoesNotExist() throws Exception {
        Integer badId = 1;
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), badId).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void TestGetSingleEventCar_DoesNotExist_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}/cars/1", resultEvent.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
