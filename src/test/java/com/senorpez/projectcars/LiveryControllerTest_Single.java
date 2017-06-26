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
import static com.senorpez.projectcars.LiveryControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest
public class LiveryControllerTest_Single {
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
