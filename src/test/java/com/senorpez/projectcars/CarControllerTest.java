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
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Enclosed.class)
public class CarControllerTest {
    private static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars.v1+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = CarControllerTest.class.getClassLoader();
    private static final String COLLECTION_SCHEMA = "cars.schema.json";
    private static final String OBJECT_SCHEMA = "car.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class CarControllerTest_Parameterized {
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
        public void GetAllCars_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

            mockMvc.perform(get("/cars").accept(MEDIA_TYPE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MEDIA_TYPE))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("id", resultCar.getId()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("carName", resultCar.getName()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(
                            hasEntry(is("_links"),
                                    hasEntry(is("self"),
                                            hasEntry("href", "http://localhost/cars/" + resultCar.getId()))))))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars")))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetAllCars_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

            mockMvc.perform(get("/cars").accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("id", resultCar.getId()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("carName", resultCar.getName()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(
                            hasEntry(is("_links"),
                                    hasEntry(is("self"),
                                            hasEntry("href", "http://localhost/cars/" + resultCar.getId()))))))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars")))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleCar_ValidId_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

            mockMvc.perform(get("/cars/{carId}", resultCar.getId()).accept(MEDIA_TYPE))
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
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/cars")))
                    .andExpect(jsonPath("$._links.pcars:class", hasEntry("href", "http://localhost/classes/" + resultCar.getCarClass().getId())))
                    .andExpect(jsonPath("$._links.pcars:liveries", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleCar_ValidId_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

            mockMvc.perform(get("/cars/{carId}", resultCar.getId()).accept(APPLICATION_JSON_UTF8))
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
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/cars")))
                    .andExpect(jsonPath("$._links.pcars:class", hasEntry("href", "http://localhost/classes/" + resultCar.getCarClass().getId())))
                    .andExpect(jsonPath("$._links.pcars:liveries", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleCar_ValidId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}", resultCar.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }

    @SpringBootTest
    public static class CarControllerTest_Single {
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
        public void GetAllCars_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars").accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }

        @Test
        public void GetSingleCar_InvalidId_ValidAcceptHeader() throws Exception {
            Integer badId = 8675309;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}", badId).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleCar_InvalidId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/8675309").accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }
}