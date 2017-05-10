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

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(tags = {"classes"})
@RequestMapping(method = {RequestMethod.GET})
class CarClassController {
    class CarClassList extends ResourceSupport {
        @JsonProperty("carClasses")
        private final Set<CarClass> carClassList;

        CarClassList() {
            this.carClassList = Application.CAR_CLASSES.stream()
                    .map(CarClassController.this::addLink)
                    .collect(Collectors.toSet());
            this.add(linkTo(methodOn(CarClassController.class).carClasses()).withSelfRel());
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
        return new CarClassList();
    }

    @RequestMapping(value = "/v1/classes/{carClassId}")
    @ApiOperation(
            value = "Returns a car class.",
            notes = "Returns a car class as specified by its ID number.",
            response = CarClass.class
    )
    CarClass carClasses(
            @ApiParam(
                    value = "ID of car class to return",
                    required = true
            )
            @PathVariable Integer carClassId) {
        return Application.CAR_CLASSES.stream()
                .filter(carClass -> carClass.getCarClassId().equals(carClassId))
                .findAny()
                .map(this::addLink)
                .orElse(null);
    }

    private CarClass addLink(CarClass carClass) {
        carClass.removeLinks();
        carClass.add(linkTo(methodOn(CarClassController.class).carClasses(carClass.getCarClassId())).withSelfRel());
        return carClass;
    }
}
