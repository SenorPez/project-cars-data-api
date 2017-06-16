package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
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
import java.util.Set;
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Enclosed.class)
public class EventControllerTest {
    private static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars.v1+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = EventControllerTest.class.getClassLoader();
    private static final String COLLECTION_SCHEMA = "events.schema.json";
    private static final String CAR_COLLECTION_SCHEMA = "cars.schema.json";
    private static final String OBJECT_SCHEMA = "event.schema.json";
    private static final String CAR_OBJECT_SCHEMA = "car.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class EventControllerTest_Parameterized_Events {
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
    }

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class EventControllerTest_Parameterized_Cars {
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
        public Integer carId;
        @Parameter(value = 1)
        public Car resultCar;

        @Parameters(name = "carId: {0}")
        public static Iterable<Object[]> parameters() {
            return Application.CARS.stream()
                    .map(car -> new Object[]{car.getId(), car})
                    .collect(Collectors.toList());
        }

        @Test
        public void GetSingleEventSingleCar_InvalidEventId_ValidCarId_ValidAcceptHeader() throws Exception {
            Integer badId = 8675309;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", badId, resultCar.getId()).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                    .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleEventSingleCar_InvalidEventId_ValidCarId_FallbackHeader() throws Exception {
            Integer badId = 86753029;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", badId, resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-events-" + badId)))
                    .andExpect(jsonPath("$.message", is("Event with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleEventSingleCar_InvalidEventId_ValidCarId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/86753029/cars/{carId}", resultCar.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class EventControllerTest_Parameterized_EventsCars {
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

        @Parameters(name = "eventId: {0}, carId: {2}")
        public static Iterable<Object[]> parameters() {
            return Application.EVENTS.stream()
                    .flatMap(event -> event.getCars().stream()
                            .map(car -> new Object[]{event.getId(), event, car.getId(), car}))
                    .collect(Collectors.toList());
        }

        @Test
        public void GetSingleEventAllCars_ValidId_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(CAR_COLLECTION_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars", resultEvent.getId()).accept(MEDIA_TYPE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MEDIA_TYPE))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("id", resultCar.getId()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("carName", resultCar.getName()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(
                            hasEntry(is("_links"),
                                    hasEntry(is("self"), containsInAnyOrder(
                                            hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                                            hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId())))))))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:event", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleEventAllCars_ValidId_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(CAR_COLLECTION_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars", resultEvent.getId()).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("id", resultCar.getId()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("carName", resultCar.getName()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(
                            hasEntry(is("_links"),
                                    hasEntry(is("self"), containsInAnyOrder(
                                            hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                                            hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId())))))))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:event", hasEntry("href", "http://localhost/events/" + resultEvent.getId())))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleEventAllCars_ValidId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars", resultEvent.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }

        @Test
        public void GetSingleEventSingleCar_ValidEventId_ValidCarId_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(CAR_OBJECT_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(MEDIA_TYPE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MEDIA_TYPE))
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
                    .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                            hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                            hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId()))))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                    .andExpect(jsonPath("$._links.pcars:class", hasEntry("href", "http://localhost/classes/" + resultCar.getCarClass().getId())))
                    .andExpect(jsonPath("$._links.pcars:liveries", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleEventSingleCar_ValidEventId_ValidCarId_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(CAR_OBJECT_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
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
                    .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                            hasEntry("href", "http://localhost/cars/" + resultCar.getId()),
                            hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars/" + resultCar.getId()))))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/cars")))
                    .andExpect(jsonPath("$._links.pcars:class", hasEntry("href", "http://localhost/classes/" + resultCar.getCarClass().getId())))
                    .andExpect(jsonPath("$._links.pcars:liveries", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleEventSingleCar_ValidEventId_ValidCarId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class EventControllerTest_Parameterized_EventsCarsNotInEvent {
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

        @Parameters(name = "eventId: {0}, carId: {2}")
        public static Iterable<Object[]> parameters() {
            /* We limit the number of foreign cars, otherwise we end up with a shitload of tests. */
            return Application.EVENTS.stream()
                    .flatMap(event -> {
                        Set<Integer> carIds = event.getCars().stream()
                                .map(Car::getId)
                                .collect(Collectors.toSet());
                        Set<Car> cars = Application.CARS.stream()
                                .filter(car -> !carIds.contains(car.getId()))
                                .limit(10)
                                .collect(Collectors.toSet());
                        return cars.stream()
                                .map(car -> new Object[]{event.getId(), event, car.getId(), car});
                    })
                    .collect(Collectors.toList());
        }

        @Test
        public void GetSingleEventSingleCar_ValidEventId_ForeignCarId_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
        }

        @Test
        public void GetSingleEventSingleCar_ValidEventId_ForeignCarId_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
        }

        @Test
        public void GetSingleEventSingleCar_ValidEventId_ForeignCarId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }

    @SpringBootTest
    public static class EventControllerTest_Single {
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
    }
}