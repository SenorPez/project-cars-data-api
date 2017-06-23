package com.senorpez.projectcars;

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
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.projectcars.RaceControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Parameterized.class)
@SpringBootTest
public class RaceControllerTest_Parameterized_Events {
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

    @Parameters(name = "eventId: {0}")
    public static Iterable<Object[]> parameters() {
        return Application.EVENTS.stream()
                .map(event -> new Object[]{event.getId(), event})
                .collect(Collectors.toList());
    }

    @Test
    public void GetAllRaces_ValidEventId_InvalidRoundId_ValidAcceptHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races", resultEvent.getId(), badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-rounds-" + badId)))
                .andExpect(jsonPath("$.message", is("Round with ID of " + badId + " not found")));
    }

    @Test
    public void GetAllRaces_ValidEventId_InvalidRoundId_FallbackHeader() throws Exception {
        Integer badId = 8675309;
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races", resultEvent.getId(), badId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-rounds-" + badId)))
                .andExpect(jsonPath("$.message", is("Round with ID of " + badId + " not found")));
    }

    @Test
    public void GetAllRaces_ValidEventId_InvalidRoundId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/8675309/races", resultEvent.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleRace_ValidEventId_InvalidRoundId_XXXRaceId_ValidAcceptHeader() throws Exception {
        Integer badId = 8675309;
        Integer inconsequentialRaceId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/{raceId}", resultEvent.getId(), badId, inconsequentialRaceId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-rounds-" + badId)))
                .andExpect(jsonPath("$.message", is("Round with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleRace_ValidEventId_InvalidRoundId_XXXRaceId_FallbackHeader() throws Exception {
        Integer badId = 8675309;
        Integer inconsequentialRaceId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/{roundId}/races/{raceId}", resultEvent.getId(), badId, inconsequentialRaceId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-rounds-" + badId)))
                .andExpect(jsonPath("$.message", is("Round with ID of " + badId + " not found")));
    }

    @Test
    public void GetSingleRace_ValidEventId_InvalidRoundId_XXXRaceId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        Integer inconsequentialRaceId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/rounds/8675309/races/{raceId}", resultEvent.getId(), inconsequentialRaceId).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }
}
