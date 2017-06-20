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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RaceControllerTest {
    private MockMvc mockMvc;
    private static final MediaType contentType = new MediaType("application", "vnd.senorpez.pcars.v1+json", StandardCharsets.UTF_8);
    private static final ClassLoader classLoader = RaceControllerTest.class.getClassLoader();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void TestGetAllRaces() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("races.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races", resultEvent.getId(), resultRound.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/races")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:round", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId())))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllRaces_JsonFallback() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("races.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races", resultEvent.getId(), resultRound.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/races")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:round", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId())))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllRaces_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races", resultEvent.getId(), resultRound.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleRace_Exists() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        Race resultRace = resultRound.getRaces().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("race.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/{raceId}", resultEvent.getId(), resultRound.getId(), resultRace.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultRace.getId())))
                .andExpect(jsonPath("$.laps", is(resultRace.getLaps())))
                .andExpect(jsonPath("$.time", is(resultRace.getTime())))
                .andExpect(jsonPath("$.type", is(resultRace.getType())));
    }

    @Test
    public void TestGetSingleRace_Exists_JsonFallback() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        Race resultRace = resultRound.getRaces().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("race.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/{raceId}", resultEvent.getId(), resultRound.getId(), resultRace.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultRace.getId())))
                .andExpect(jsonPath("$.laps", is(resultRace.getLaps())))
                .andExpect(jsonPath("$.time", is(resultRace.getTime())))
                .andExpect(jsonPath("$.type", is(resultRace.getType())));
    }

    @Test
    public void TestGetSingleRace_Exists_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        Race resultRace = resultRound.getRaces().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("race.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/{raceId}", resultEvent.getId(), resultRound.getId(), resultRace.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void TestGetSingleRace_DoesNotExist() throws Exception {
        Integer badId = 8675309;
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("race.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/{raceId}", resultEvent.getId(), resultRound.getId(), badId).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-races-" + badId)))
                .andExpect(jsonPath("$.message", is("Race with ID of " + badId + " not found")));
    }

    @Test
    public void TestGetSingleRace_DoesNotExist_InvalidAcceptHeader() throws Exception {
        Event resultEvent = Application.EVENTS.stream().findAny().orElse(null);
        Round resultRound = resultEvent.getRounds().stream().findAny().orElse(null);
        InputStream jsonSchema = classLoader.getResourceAsStream("race.schema.json");

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/8675309", resultEvent.getId(), resultRound.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
