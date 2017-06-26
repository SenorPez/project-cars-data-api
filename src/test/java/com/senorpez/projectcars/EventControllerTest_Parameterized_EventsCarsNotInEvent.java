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
import java.util.Set;
import java.util.stream.Collectors;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static com.senorpez.projectcars.EventControllerTest.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(Parameterized.class)
@SpringBootTest
public class EventControllerTest_Parameterized_EventsCarsNotInEvent {
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
    public Integer carId;
    @Parameter(value = 3)
    public Car resultCar;

    @Parameters(name = "eventId: {0}, carId: {2}")
    public static Iterable<Object[]> parameters() {
        /* We limit the number of foreign cars, otherwise we end up with a shitload of tests. */
        return Application.EVENTS.stream()
                .limit(10)
                .flatMap(event -> {
                    Set<Integer> carIds = event.getCars().stream()
                            .map(Car::getId)
                            .collect(Collectors.toSet());
                    Set<Car> cars = Application.CARS.stream()
                            .filter(car -> !carIds.contains(car.getId()))
                            .limit(10)
                            .collect(Collectors.toSet());
                    return cars.stream()
                            .map(car -> new Object[]{event.getId(), event, car.getId(), car});
                })
                .collect(Collectors.toList());
    }

    @Test
    public void GetSingleEventSingleCar_ValidEventId_ForeignCarId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCar_ValidEventId_ForeignCarId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCar_ValidEventId_ForeignCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}", resultEvent.getId(), resultCar.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarClass_ValidEventId_ForeignCarId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", resultEvent.getId(), resultCar.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarClass_ValidEventId_ForeignCarId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", resultEvent.getId(), resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarClass_ValidEventId_ForeignCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/class", resultEvent.getId(), resultCar.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_ForeignCarId_ValidAcceptHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), resultCar.getId()).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_ForeignCarId_FallbackHeader() throws Exception {
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), resultCar.getId()).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarAllLiveries_ValidEventId_ForeignCarId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries", resultEvent.getId(), resultCar.getId()).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_ForeignCarId_XXXLiveryId_ValidAcceptHeader() throws Exception {
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), resultCar.getId(), inconsequentialLiveryId).accept(MEDIA_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_ForeignCarId_XXXLiveryId_FallbackHeader() throws Exception {
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), resultCar.getId(), inconsequentialLiveryId).accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("404-cars-" + resultCar.getId())))
                .andExpect(jsonPath("$.message", is("Car with ID of " + resultCar.getId() + " not found")));
    }

    @Test
    public void GetSingleEventSingleCarSingleLivery_ValidEventId_ForeignCarId_XXXLiveryId_InvalidAcceptHeader() throws Exception {
        MediaType contentType = new MediaType("application", "vnd.senorpez.badrequest+json", UTF_8);
        Integer inconsequentialLiveryId = new Double(Math.random()).intValue();
        InputStream jsonSchema = CLASS_LOADER.getResourceAsStream(ERROR_SCHEMA);

        mockMvc.perform(get("/events/{eventId}/cars/{carId}/liveries/{liveryId}", resultEvent.getId(), resultCar.getId(), inconsequentialLiveryId).accept(contentType))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.code", is("406")))
                .andExpect(jsonPath("$.message", is("Accept header incorrect")));
    }

}
