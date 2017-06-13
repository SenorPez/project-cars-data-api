package com.senorpez.projectcars;

import org.hamcrest.Matcher;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrackControllerTest {
    private MockMvc mockMvc;
    private static final MediaType contentType = new MediaType("application", "vnd.senorpez.pcars.v1+json", StandardCharsets.UTF_8);
    private static final ClassLoader classLoader = TrackControllerTest.class.getClassLoader();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void TestGetAllTracks() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("tracks.schema.json");

        mockMvc.perform(get("/tracks").header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:track", hasSize(100)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllTracks_JsonFallback() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("tracks.schema.json");

        mockMvc.perform(get("/tracks").header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:track", hasSize(100)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllTracks_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/tracks").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleTrack_Exists() throws Exception {
        Track resultTrack = Application.TRACKS.stream().findAny().orElse(null);

        Matcher pitEntryMatcher;
        if (resultTrack.getPitEntry() != null) {
            pitEntryMatcher = contains(
                    closeTo(Double.valueOf(resultTrack.getPitEntry().get(0)), 0.25),
                    closeTo(Double.valueOf(resultTrack.getPitEntry().get(1)), 0.25));
        } else {
            pitEntryMatcher = nullValue();
        }

        Matcher pitExitMatcher;
        if (resultTrack.getPitEntry() != null) {
            pitExitMatcher = contains(
                    closeTo(Double.valueOf(resultTrack.getPitExit().get(0)), 0.25),
                    closeTo(Double.valueOf(resultTrack.getPitExit().get(1)), 0.25));
        } else {
            pitExitMatcher = nullValue();
        }

        InputStream jsonSchema = classLoader.getResourceAsStream("track.schema.json");

        mockMvc.perform(get("/tracks/{trackId}", resultTrack.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultTrack.getId())))
                .andExpect(jsonPath("$.name", is(resultTrack.getName())))
                .andExpect(jsonPath("$.location", is(resultTrack.getLocation())))
                .andExpect(jsonPath("$.variation", is(resultTrack.getVariation())))
                .andExpect(jsonPath("$.length", closeTo(Double.valueOf(resultTrack.getLength()), 0.001)))
                .andExpect(jsonPath("$.gridSize", is(resultTrack.getGridSize())))
                .andExpect(jsonPath("$.pitEntry", pitEntryMatcher))
                .andExpect(jsonPath("$.pitExit", pitExitMatcher))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/tracks/" + resultTrack.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:tracks", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleTrack_Exists_JsonFallback() throws Exception {
        Track resultTrack = Application.TRACKS.stream().findAny().orElse(null);

        Matcher pitEntryMatcher;
        if (resultTrack.getPitEntry() != null) {
            pitEntryMatcher = contains(
                    closeTo(Double.valueOf(resultTrack.getPitEntry().get(0)), 0.25),
                    closeTo(Double.valueOf(resultTrack.getPitEntry().get(1)), 0.25));
        } else {
            pitEntryMatcher = nullValue();
        }

        Matcher pitExitMatcher;
        if (resultTrack.getPitEntry() != null) {
            pitExitMatcher = contains(
                    closeTo(Double.valueOf(resultTrack.getPitExit().get(0)), 0.25),
                    closeTo(Double.valueOf(resultTrack.getPitExit().get(1)), 0.25));
        } else {
            pitExitMatcher = nullValue();
        }

        InputStream jsonSchema = classLoader.getResourceAsStream("track.schema.json");

        mockMvc.perform(get("/tracks/{trackId}", resultTrack.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultTrack.getId())))
                .andExpect(jsonPath("$.name", is(resultTrack.getName())))
                .andExpect(jsonPath("$.location", is(resultTrack.getLocation())))
                .andExpect(jsonPath("$.variation", is(resultTrack.getVariation())))
                .andExpect(jsonPath("$.length", closeTo(Double.valueOf(resultTrack.getLength()), 0.001)))
                .andExpect(jsonPath("$.gridSize", is(resultTrack.getGridSize())))
                .andExpect(jsonPath("$.pitEntry", pitEntryMatcher))
                .andExpect(jsonPath("$.pitExit", pitExitMatcher))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/tracks/" + resultTrack.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:tracks", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleTrack_Exists_InvalidAcceptHeader() throws Exception {
        Track resultTrack = Application.TRACKS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/tracks/{trackId}", resultTrack.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleTrack_DoesNotExist() throws Exception {
        Integer badId = 1;
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/tracks/{trackId}", badId).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-tracks-" + badId)))
                .andExpect(jsonPath("$.message", is("Track with ID of " + badId + " not found")));
    }

    @Test
    public void TestGetSingleTrack_DoesNotExist_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/tracks/1").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
