package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Api(tags = {"classes"})
@RequestMapping(method = {RequestMethod.GET})
class CarClassController {
    class CarClassList extends ResourceSupport {
        @JsonProperty("carClasses")
        private final Set<CarClass> carClassList;

        CarClassList(Set<CarClass> carClassList) {
            this.carClassList = carClassList;
        }
    }

    @RequestMapping(value = "/v1/classes")
    @ApiOperation(
            value = "Lists all car classes available.",
            notes = "Returns a list of all car classes available through the Project CARS Data API.",
            response = CarClass.class,
            responseContainer = "List"
    )
    CarClassList carClasses() {
        return new CarClassList(Application.CAR_CLASSES);
    }

    @RequestMapping(value = "/v1/classes/{carClassId}")
    @ApiOperation(
            value = "Returns a car class.",
            notes = "Returns a car class as specified by its ID number.",
            response = CarClass.class
    )
    public CarClass carClasses(
            @ApiParam(
                    value = "ID of car class to return",
                    required = true
            )
            @PathVariable Integer carClassId) {
        return Application.CAR_CLASSES.stream()
                .filter(carClass -> carClass.getCarClassId().equals(carClassId))
                .findAny()
                .orElse(null);
    }
}
