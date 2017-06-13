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
public class Car2ControllerTest {
    private MockMvc mockMvc;
    private static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars2.v0+json", StandardCharsets.UTF_8);
    private static final ClassLoader CLASS_LOADER = Car2ControllerTest.class.getClassLoader();
    private static final String OBJECT_SCHEMA = "car2.schema.json";
    private static final String ERROR_SCHEMA = "error.schema.json";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void GetSingleCar_ValidId_ValidAcceptHeader() throws Exception {
        Car2 resultCar = getResultCar();
        InputStream jsonSchema  = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

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
        Car2 resultCar = getResultCar();
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", StandardCharsets.UTF_8);
        InputStream jsonSchema  = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/{carId}", resultCar.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleCar_InvalidId_ValidAcceptHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema  = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/{carId}", badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleCar_InvalidId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", StandardCharsets.UTF_8);
        InputStream jsonSchema  = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/8675309").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    private static Car2 getResultCar() throws Exception {
        return Application.CARS2.stream().findAny().orElseThrow(() -> new Exception("Error selecting car"));
    }
}
