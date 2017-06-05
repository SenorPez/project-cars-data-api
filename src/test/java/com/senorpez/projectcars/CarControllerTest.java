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
import java.util.Arrays;

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
    public void TestGetAllCars() throws Exception {
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

    @Test
    public void TestGetSingleCar_Exists() throws Exception {
        Car resultCar = Application.CARS.stream().findAny().orElse(null);

        mockMvc.perform(get("/cars/" + resultCar.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", allOf(Arrays.asList(
                        hasKey("id"),
                        hasKey("manufacturer"),
                        hasKey("model"),
                        hasKey("country"),
                        hasKey("year"),
                        hasKey("drivetrain"),
                        hasKey("enginePosition"),
                        hasKey("engineType"),
                        hasKey("topSpeed"),
                        hasKey("horsepower"),
                        hasKey("acceleration"),
                        hasKey("braking"),
                        hasKey("weight"),
                        hasKey("torque"),
                        hasKey("weightBalance"),
                        hasKey("wheelbase"),
                        hasKey("shiftPattern"),
                        hasKey("shifter"),
                        hasKey("gears"),
                        hasKey("dlc"),
                        hasKey("carClass"),
                        hasKey("_links")))))
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
                .andExpect(jsonPath("$._links", allOf(hasKey("self"), hasKey("index"), hasKey("pcars:cars"), hasKey("curies"))))
                .andExpect(jsonPath("$._links.self", hasEntry("href", "http://localhost/cars/" + resultCar.getId())))
                .andExpect(jsonPath("$._links.index", hasEntry("href", "http://localhost/")))
                .andExpect(jsonPath("$._links.pcars:cars", hasEntry("href", "http://localhost/cars")))
                .andExpect(jsonPath("$._links.curies", hasSize(1)))
                .andExpect(jsonPath("$._links.curies[0]", both(allOf(
                        hasEntry("href", "http://localhost/{rel}"),
                        hasEntry("name", "pcars")))
                        .and(hasEntry("templated", true))));
    }
}