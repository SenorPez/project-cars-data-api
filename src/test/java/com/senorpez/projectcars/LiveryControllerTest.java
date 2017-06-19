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
public class LiveryControllerTest {
    private static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars.v1+json", UTF_8);
    private static final ClassLoader CLASS_LOADER = LiveryControllerTest.class.getClassLoader();
    private static final String COLLECTION_SCHEMA = "liveries.schema.json";
    private static final String OBJECT_SCHEMA = "livery.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class LiveryControllerTest_ParameterizedCars {
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
        public void GetAllLiveries_ValidCarId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries", resultCar.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }

        @Test
        public void GetSingleLivery_ValidCarId_InvalidLiveryId_ValidAcceptHeader() throws Exception {
            Integer badId = 8675309;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", resultCar.getId(), badId).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-liveries-" + badId)))
                    .andExpect(jsonPath("$.message", is("Livery with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleLivery_ValidCarId_InvalidLiveryId_FallbackHeader() throws Exception {
            Integer badId = 8675309;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", resultCar.getId(), badId).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-liveries-" + badId)))
                    .andExpect(jsonPath("$.message", is("Livery with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleLivery_ValidCarId_InvalidLiveryId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/8675309", resultCar.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }

    @RunWith(Parameterized.class)
    @SpringBootTest
    public static class LiveryControllerTest_ParameterizedCarsLiveries {
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
        @Parameter(value = 2)
        public Integer liveryId;
        @Parameter(value = 3)
        public Livery resultLivery;

        @Parameters(name = "carId: {0}, liveryId: {2}")
        public static Iterable<Object[]> parameters() {
            return Application.CARS.stream()
                    .flatMap(car -> car.getLiveries().stream()
                            .map(livery -> new Object[]{car.getId(), car, livery.getId(), livery}))
                    .collect(Collectors.toList());
        }

        @Test
        public void GetAllLiveries_ValidCarId_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries", resultCar.getId()).accept(MEDIA_TYPE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MEDIA_TYPE))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("id", resultLivery.getId()))))
                    .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("name", resultLivery.getName()))))
                    .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(
                            hasEntry(is("_links"),
                                    hasEntry(is("self"),
                                            hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()))))))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetAllLiveries_ValidCarId_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries", resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("id", resultLivery.getId()))))
                    .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(hasEntry("name", resultLivery.getName()))))
                    .andExpect(jsonPath("$._embedded.pcars:livery", hasItem(
                            hasEntry(is("_links"),
                                    hasEntry(is("self"),
                                            hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId()))))))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleLivery_ValidCarId_ValidLiveryId_ValidAcceptHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", resultCar.getId(), resultLivery.getId()).accept(MEDIA_TYPE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MEDIA_TYPE))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.id", is(resultLivery.getId())))
                    .andExpect(jsonPath("$.name", is(resultLivery.getName())))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId())))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:liveries", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleLivery_ValidCarId_ValidLiveryId_FallbackHeader() throws Exception {
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", resultCar.getId(), resultLivery.getId()).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.id", is(resultLivery.getId())))
                    .andExpect(jsonPath("$.name", is(resultLivery.getName())))
                    .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries/" + resultLivery.getId())))
                    .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                    .andExpect(jsonPath("$._links.pcars:liveries", hasEntry("href", "http://localhost/cars/" + resultCar.getId() + "/liveries")))
                    .andExpect(jsonPath("$._links.curies", everyItem(
                            allOf(
                                    hasEntry("href", (Object) "http://localhost/{rel}"),
                                    hasEntry("name", (Object) "pcars"),
                                    hasEntry("templated", (Object) true)))));
        }

        @Test
        public void GetSingleLivery_ValidCarId_ValidLiveryId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", resultCar.getId(), resultLivery.getId()).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }

    @SpringBootTest
    public static class LiveryControllerTest_Single {
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
        public void GetAllLiveries_InvalidCarId_ValidAcceptHeader() throws Exception {
            Integer badId = 1;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{cardId}/liveries", badId).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
        }

        @Test
        public void GetAllLiveries_InvalidCarId_FallbackHeader() throws Exception {
            Integer badId = 1;
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{cardId}/liveries", badId).accept(APPLICATION_JSON_UTF8))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
        }

        @Test
        public void GetAllLiveries_InvalidCarId_InvalidAcceptHeader() throws Exception {
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/1/liveries").accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }

        @Test
        public void GetSingleLivery_InvalidCarId_XXXLiveryId_ValidAcceptHeader() throws Exception {
            Integer badId = 1;
            Integer inconsequentalLiveryId = new Double(Math.random()).intValue();
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", badId, inconsequentalLiveryId).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleLivery_InvalidCarId_XXXLiveryId_FallbackHeader() throws Exception {
            Integer badId = 1;
            Integer inconsequentalLiveryId = new Double(Math.random()).intValue();
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/{carId}/liveries/{liveryId}", badId, inconsequentalLiveryId).accept(MEDIA_TYPE))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                    .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
        }

        @Test
        public void GetSingleLivery_InvalidCarId_XXXLiveryId_InvalidAcceptHeader() throws Exception {
            Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
            MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
            InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

            mockMvc.perform(get("/cars/1/liveries/{liveryId}", inconsequentialLiveryId).accept(contentType))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                    .andExpect(jsonPath("$.code", is("406")))
                    .andExpect(jsonPath("$.message", is("Accept header incorrect")));
        }
    }
}
