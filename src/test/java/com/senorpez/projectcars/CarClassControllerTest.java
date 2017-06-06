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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarClassControllerTest {
    private MockMvc mockMvc;
    private static final MediaType contentType = new MediaType("application", "vnd.senorpez.pcars.v1+json", StandardCharsets.UTF_8);
    private static final ClassLoader classLoader = CarControllerTest.class.getClassLoader();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void TestGetAllCarClasses() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("classes.schema.json");

        mockMvc.perform(get("/classes").header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:class", hasSize(47)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/classes")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllCarClasses_JsonFallback() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("classes.schema.json");

        mockMvc.perform(get("/classes").header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:class", hasSize(47)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/classes")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllCarClasses_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/classes").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("Internal server error.")))
                .andExpect(jsonPath("$.error.message", is("SERVER_ERROR")));
    }

    @Test
    public void TestGetSingleCarClass_Exists() throws Exception {
        CarClass resultClass = Application.CAR_CLASSES.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("class.schema.json");

        mockMvc.perform(get("/classes/{classId}", resultClass.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultClass.getId())))
                .andExpect(jsonPath("$.name", is(resultClass.getName())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/classes/" + resultClass.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:classes", hasEntry("href", "http://localhost/classes")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleClass_Exists_JsonFallback() throws Exception {
        CarClass resultClass = Application.CAR_CLASSES.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("class.schema.json");

        mockMvc.perform(get("/classes/{classId}", resultClass.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultClass.getId())))
                .andExpect(jsonPath("$.name", is(resultClass.getName())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/classes/" + resultClass.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:classes", hasEntry("href", "http://localhost/classes")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleCarClass_Exists_InvalidAcceptHeader() throws Exception {
        CarClass resultClass = Application.CAR_CLASSES.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/classes/{classId}", resultClass.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("Internal server error.")))
                .andExpect(jsonPath("$.error.message", is("SERVER_ERROR")));

    }

    @Test
    public void TestGetSingleCarClass_DoesNotExist() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/classes/1").header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("CAR_CLASS_NOT_FOUND")))
                .andExpect(jsonPath("$.error.message", is("Car Class Not Found.")));

    }

    @Test
    public void TestGetSingleCarClass_DoesNotExist_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/classes/1").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("Internal server error.")))
                .andExpect(jsonPath("$.error.message", is("SERVER_ERROR")));
    }
}
