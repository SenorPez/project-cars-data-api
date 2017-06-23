package com.senorpez.projectcars;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
import static com.senorpez.projectcars.Track2ControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Parameterized.class)
@SpringBootTest
public class Track2ControllerTest_Parameterized {
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

    @Parameterized.Parameter
    public Integer trackId;
    @Parameterized.Parameter(value = 1)
    public Track2 resultTrack;

    @Parameterized.Parameters(name = "trackId: {0}")
    public static Iterable<Object[]> parameters() {
        return Application.TRACKS2.stream()
                .limit(10)
                .map(track -> new Object[]{track.getId(), track})
                .collect(Collectors.toList());
    }

    @Test
    public void GetAllTracks_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

        mockMvc.perform(get("/tracks").accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("id", resultTrack.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("country", resultTrack.getCountry()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("name", resultTrack.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("location", resultTrack.getLocation()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("variation", resultTrack.getVariation()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("verified", resultTrack.getVerified()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        hasEntry("href", "http://localhost/tracks/" + resultTrack.getId()))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleTrack_ValidId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

        mockMvc.perform(get("/tracks/{trackId}", resultTrack.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultTrack.getId())))
                .andExpect(jsonPath("$.country", is(resultTrack.getCountry())))
                .andExpect(jsonPath("$.name", is(resultTrack.getName())))
                .andExpect(jsonPath("$.location", is(resultTrack.getLocation())))
                .andExpect(jsonPath("$.variation", is(resultTrack.getVariation())))
                .andExpect(jsonPath("$.verified", is(resultTrack.getVerified())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/tracks/" + resultTrack.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:tracks", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleTrack_ValidId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/tracks/{trackId}", resultTrack.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
