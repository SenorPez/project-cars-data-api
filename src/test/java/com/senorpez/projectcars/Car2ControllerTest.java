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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Enclosed.class)
public class Car2ControllerTest {
    private static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars2.v0+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = Car2ControllerTest.class.getClassLoader();
    private static final String COLLECTION_SCHEMA = "cars2.schema.json";
    private static final String OBJECT_SCHEMA = "car2.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class Car2ControllerTest_Parameterized {
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
        public Car2 resultCar;

        @Parameters(name = "carId: {0}")
        public static Iterable<Object[]> parameters() {
            return Application.CARS2.stream()
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
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("year", resultCar.getYear()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("manufacturer", resultCar.getManufacturer()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("model", resultCar.getModel()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("verified", resultCar.getVerified()))))
                    .andExpect(jsonPath("$._embedded.pcars:car", hasItem(hasEntry("dlc", resultCar.getDlc()))))
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
                    .andExpect(jsonPath("$.year", is(resultCar.getYear())))
                    .andExpect(jsonPath("$.manufacturer", is(resultCar.getManufacturer())))
                    .andExpect(jsonPath("$.model", is(resultCar.getModel())))
                    .andExpect(jsonPath("$.verified", is(resultCar.getVerified())))
                    .andExpect(jsonPath("$.dlc", is(resultCar.getDlc())))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/cars")))
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
    public static class Car2ControllerTest_Single {
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
