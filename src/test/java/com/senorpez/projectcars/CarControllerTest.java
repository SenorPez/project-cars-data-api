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

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarControllerTest {
    private MockMvc mockMvc;
    private MediaType contentType = new MediaType("application", "vnd.senorpez.pcars.v1+json", StandardCharsets.UTF_8);

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void cars() throws Exception {
        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", allOf(hasKey("_embedded"), hasKey("_links"))))
                .andExpect(jsonPath("$._embedded", hasKey("pcars:car")))
                .andExpect(jsonPath("$._embedded.pcars:car", hasSize(125)))
                .andExpect(jsonPath("$._embedded.pcars:car", everyItem(hasEntry(is("id"), isA(Integer.class)))))
                .andExpect(jsonPath("$._embedded.pcars:car", everyItem(hasEntry(is("carName"), isA(String.class)))))
                .andExpect(jsonPath("$._embedded.pcars:car", everyItem(hasKey("_links"))))
                // TODO: 6/4/2017 Determine how to test each items "_links" property to make sure they're formed properly. 
                .andExpect(jsonPath("$._links", allOf(hasKey("self"), hasKey("index"), hasKey("curies"))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));

    }
}