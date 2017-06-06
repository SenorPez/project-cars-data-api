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
public class CarControllerTest {
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
    public void TestGetAllCars() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("cars.schema.json");

        mockMvc.perform(get("/cars").header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:car", hasSize(125)))
                // TODO: 6/4/2017 Determine how to test each items "_links" property to make sure they're formed properly.
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllCars_JsonFallback() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("cars.schema.json");

        mockMvc.perform(get("/cars").header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$._embedded.pcars:car", hasSize(125)))
                // TODO: 6/4/2017 Determine how to test each items "_links" property to make sure they're formed properly.
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars")))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetAllCars_InvalidAcceptHeader() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/cars").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("Internal server error.")))
                .andExpect(jsonPath("$.error.message", is("SERVER_ERROR")));
    }

    @Test
    public void TestGetSingleCar_Exists() throws Exception {
        Car resultCar = Application.CARS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("car.schema.json");

        mockMvc.perform(get("/cars/{carId}", resultCar.getId()).header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultCar.getId())))
                .andExpect(jsonPath("$.manufacturer", is(resultCar.getManufacturer())))
                .andExpect(jsonPath("$.model", is(resultCar.getModel())))
                .andExpect(jsonPath("$.country", is(resultCar.getCountry())))
                .andExpect(jsonPath("$.year", is(resultCar.getYear())))
                .andExpect(jsonPath("$.drivetrain", is(resultCar.getDrivetrain().toString())))
                .andExpect(jsonPath("$.enginePosition", is(resultCar.getEnginePosition().getDisplayString())))
                .andExpect(jsonPath("$.engineType", is(resultCar.getEngineType())))
                .andExpect(jsonPath("$.topSpeed", is(resultCar.getTopSpeed())))
                .andExpect(jsonPath("$.horsepower", is(resultCar.getHorsepower())))
                .andExpect(jsonPath("$.acceleration", closeTo(Double.valueOf(resultCar.getAcceleration()), 0.001)))
                .andExpect(jsonPath("$.braking", closeTo(Double.valueOf(resultCar.getBraking()), 0.001)))
                .andExpect(jsonPath("$.weight", is(resultCar.getWeight())))
                .andExpect(jsonPath("$.torque", is(resultCar.getTorque())))
                .andExpect(jsonPath("$.weightBalance", is(resultCar.getWeightBalance())))
                .andExpect(jsonPath("$.wheelbase", closeTo(Double.valueOf(resultCar.getWheelbase()), 0.001)))
                .andExpect(jsonPath("$.shiftPattern", is(resultCar.getShiftPattern().getDisplayString())))
                .andExpect(jsonPath("$.shifter", is(resultCar.getShifter().getDisplayString())))
                .andExpect(jsonPath("$.gears", is(resultCar.getGears())))
                .andExpect(jsonPath("$.dlc", is(resultCar.getDlc())))
                .andExpect(jsonPath("$.carClass", is(resultCar.getCarClass())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/cars")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleCar_Exists_JsonFallback() throws Exception {
        Car resultCar = Application.CARS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("car.schema.json");

        mockMvc.perform(get("/cars/{carId}", resultCar.getId()).header("accept", "application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.id", is(resultCar.getId())))
                .andExpect(jsonPath("$.manufacturer", is(resultCar.getManufacturer())))
                .andExpect(jsonPath("$.model", is(resultCar.getModel())))
                .andExpect(jsonPath("$.country", is(resultCar.getCountry())))
                .andExpect(jsonPath("$.year", is(resultCar.getYear())))
                .andExpect(jsonPath("$.drivetrain", is(resultCar.getDrivetrain().toString())))
                .andExpect(jsonPath("$.enginePosition", is(resultCar.getEnginePosition().getDisplayString())))
                .andExpect(jsonPath("$.engineType", is(resultCar.getEngineType())))
                .andExpect(jsonPath("$.topSpeed", is(resultCar.getTopSpeed())))
                .andExpect(jsonPath("$.horsepower", is(resultCar.getHorsepower())))
                .andExpect(jsonPath("$.acceleration", closeTo(Double.valueOf(resultCar.getAcceleration()), 0.001)))
                .andExpect(jsonPath("$.braking", closeTo(Double.valueOf(resultCar.getBraking()), 0.001)))
                .andExpect(jsonPath("$.weight", is(resultCar.getWeight())))
                .andExpect(jsonPath("$.torque", is(resultCar.getTorque())))
                .andExpect(jsonPath("$.weightBalance", is(resultCar.getWeightBalance())))
                .andExpect(jsonPath("$.wheelbase", closeTo(Double.valueOf(resultCar.getWheelbase()), 0.001)))
                .andExpect(jsonPath("$.shiftPattern", is(resultCar.getShiftPattern().getDisplayString())))
                .andExpect(jsonPath("$.shifter", is(resultCar.getShifter().getDisplayString())))
                .andExpect(jsonPath("$.gears", is(resultCar.getGears())))
                .andExpect(jsonPath("$.dlc", is(resultCar.getDlc())))
                .andExpect(jsonPath("$.carClass", is(resultCar.getCarClass())))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/cars")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }

    @Test
    public void TestGetSingleCar_Exists_InvalidAcceptHeader() throws Exception {
        Car resultCar = Application.CARS.stream().findAny().orElse(null);

        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/cars/{carId}", resultCar.getId()).header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("Internal server error.")))
                .andExpect(jsonPath("$.error.message", is("SERVER_ERROR")));
    }

    @Test
    public void TestGetSingleCar_DoesNotExist() throws Exception {
        InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/cars/1").header("accept", "application/vnd.senorpez.pcars.v1+json"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("CAR_NOT_FOUND")))
                .andExpect(jsonPath("$.error.message", is("Car Not Found.")));
    }

    @Test
    public void TestGetSingleCar_DoesNotExist_InvalidAcceptHeader() throws Exception {
       InputStream jsonSchema = classLoader.getResourceAsStream("error.schema.json");

        mockMvc.perform(get("/cars/1").header("accept", "application/vnd.senorpez.pcars2.v1+json"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(matchesJsonSchema(jsonSchema)))
                .andExpect(jsonPath("$.error.code", is("Internal server error.")))
                .andExpect(jsonPath("$.error.message", is("SERVER_ERROR")));
    }
}