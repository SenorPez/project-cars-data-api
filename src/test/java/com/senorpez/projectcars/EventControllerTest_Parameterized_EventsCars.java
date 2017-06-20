package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
public class EventControllerTest_Parameterized_EventsCars {
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

    @Parameterized.Parameter
    public Integer eventId;
    @Parameterized.Parameter(value = 1)
    public Event resultEvent;
    @Parameterized.Parameter(value = 2)
    public Integer carId;
    @Parameterized.Parameter(value = 3)
    public Car resultCar;

    @Parameterized.Parameters(name = "eventId: {0}, carId: {2}")
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
