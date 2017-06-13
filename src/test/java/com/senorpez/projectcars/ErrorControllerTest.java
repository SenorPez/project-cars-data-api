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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ErrorControllerTest {
    private MockMvc mockMvc;
    private static final MediaType contentType = new MediaType("application", "vnd.senorpez.pcars.v1+json", StandardCharsets.UTF_8);
    private static final ClassLoader classLoader = ErrorControllerTest.class.getClassLoader();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void TestBadURI() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/error"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("PAGE_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Page Not Found.")));
    }
}
