package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.projectcars.CarControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
public class CarControllerTest_Single {
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
    public void GetSingleCar_InvalidId_FallbackHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/{carId}", badId).accept(APPLICATION_JSON_UTF8))
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

    @Test
    public void GetSingleCarClass_InvalidId_ValidAcceptHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/{carId}/class", badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleCarClass_InvalidId_FallbackHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/{carId}/class", badId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + badId)))
                .andExpect(jsonPath("$.message", is("Car with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleCarClass_InvalidId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/cars/8675309/class").accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
