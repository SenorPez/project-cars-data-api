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
import static com.senorpez.projectcars.LiveryControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Parameterized.class)
@SpringBootTest
public class LiveryControllerTest_ParameterizedCarsLiveries {
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
                .limit(10)
                .flatMap(car -> car.getLiveries().stream()
                        .limit(10)
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
                .andExpect(jsonPath("$._links.pcars:car", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
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
                .andExpect(jsonPath("$._links.pcars:car", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
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
