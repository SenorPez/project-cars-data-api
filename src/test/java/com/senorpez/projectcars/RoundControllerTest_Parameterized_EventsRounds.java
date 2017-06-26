package com.senorpez.projectcars;

import org.hamcrest.Matcher;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.projectcars.RoundControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Parameterized.class)
@SpringBootTest
public class RoundControllerTest_Parameterized_EventsRounds {
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
    public Integer eventId;
    @Parameter(value = 1)
    public Event resultEvent;
    @Parameter(value = 2)
    public Integer roundId;
    @Parameter(value = 3)
    public Round resultRound;

    @Parameters(name = "eventId: {0}, roundId: {2}")
    public static Iterable<Object[]> parameters() {
        return Application.EVENTS.stream()
                .limit(10)
                .flatMap(event -> event.getRounds().stream()
                        .limit(10)
                        .map(round -> new Object[]{event.getId(), event, round.getId(), round}))
                .collect(Collectors.toList());
    }

    @Test
    public void GetAllRounds_ValidEventId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds", resultEvent.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:round", hasItem(hasEntry("id", resultRound.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:round", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId()))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetAllRounds_ValidEventId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(COLLECTION_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds", resultEvent.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:round", hasItem(hasEntry("id", resultRound.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:round", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId()))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleRound_ValidEventId_ValidRoundId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

        final Track resultTrack = resultRound.getTrack();

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}", resultEvent.getId(), resultRound.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultRound.getId())))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("id", resultTrack.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("name", resultTrack.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        containsInAnyOrder(
                                                hasEntry("href", "http://localhost/tracks/" + resultTrack.getId()),
                                                hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/track")))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:rounds", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.pcars:races", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/races")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleRound_ValidEventId_ValidRoundId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(OBJECT_SCHEMA);

        final Track resultTrack = resultRound.getTrack();

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}", resultEvent.getId(), resultRound.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultRound.getId())))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("id", resultTrack.getId()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(hasEntry("name", resultTrack.getName()))))
                .andExpect(jsonPath("$._embedded.pcars:track", hasItem(
                        hasEntry(is("_links"),
                                hasEntry(is("self"),
                                        containsInAnyOrder(
                                                hasEntry("href", "http://localhost/tracks/" + resultTrack.getId()),
                                                hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/track")))))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:rounds", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds")))
                .andExpect(jsonPath("$._links.pcars:races", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/races")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleRound_ValidEventId_ValidRoundId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}", resultEvent.getId(), resultRound.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    private static Matcher<?> pitMatcher(List<Float> pitCoordinates) {
        return pitCoordinates == null ? nullValue() : contains(
                closeTo(Double.valueOf(pitCoordinates.get(0)), 0.25),
                closeTo(Double.valueOf(pitCoordinates.get(1)), 0.25));
    }

    @Test
    public void GetSingleRoundTrack_ValidEventId_ValidRoundId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(TRACK_OBJECT_SCHEMA);
        final Track resultTrack = resultRound.getTrack();

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/track", resultEvent.getId(), resultRound.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MEDIA_TYPE))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultTrack.getId())))
                .andExpect(jsonPath("$.location", is(resultTrack.getLocation())))
                .andExpect(jsonPath("$.variation", is(resultTrack.getVariation())))
                .andExpect(jsonPath("$.length", closeTo(Double.valueOf(resultTrack.getLength()), 0.001)))
                .andExpect(jsonPath("$.pitEntry", pitMatcher(resultTrack.getPitEntry())))
                .andExpect(jsonPath("$.pitExit", pitMatcher(resultTrack.getPitExit())))
                .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                        hasEntry("href", "http://localhost/tracks/" + resultTrack.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/track"))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:round", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId())))
                .andExpect(jsonPath("$._links.pcars:tracks", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleRoundTrack_ValidEventId_ValidRoundId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(TRACK_OBJECT_SCHEMA);
        final Track resultTrack = resultRound.getTrack();

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/track", resultEvent.getId(), resultRound.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultTrack.getId())))
                .andExpect(jsonPath("$.location", is(resultTrack.getLocation())))
                .andExpect(jsonPath("$.variation", is(resultTrack.getVariation())))
                .andExpect(jsonPath("$.length", closeTo(Double.valueOf(resultTrack.getLength()), 0.001)))
                .andExpect(jsonPath("$.pitEntry", pitMatcher(resultTrack.getPitEntry())))
                .andExpect(jsonPath("$.pitExit", pitMatcher(resultTrack.getPitExit())))
                .andExpect(jsonPath("$._links.self", containsInAnyOrder(
                        hasEntry("href", "http://localhost/tracks/" + resultTrack.getId()),
                        hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId() + "/track"))))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:round", hasEntry("href", "http://localhost/events/" + resultEvent.getId() + "/rounds/" + resultRound.getId())))
                .andExpect(jsonPath("$._links.pcars:tracks", hasEntry("href", "http://localhost/tracks")))
                .andExpect(jsonPath("$._links.curies", everyItem(
                        allOf(
                                hasEntry("href", (Object) "http://localhost/{rel}"),
                                hasEntry("name", (Object) "pcars"),
                                hasEntry("templated", (Object) true)))));
    }

    @Test
    public void GetSingleRoundTrack_ValidEventId_ValidRoundId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/track", resultEvent.getId(), resultRound.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
