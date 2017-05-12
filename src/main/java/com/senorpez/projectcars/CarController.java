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
@Api(tags = {"cars"})
@RequestMapping(method = {RequestMethod.GET})
class CarController {
    class CarList extends ResourceSupport {
        @JsonProperty("cars")
        private final Set<Car> carList;

        CarList() {
            this.carList = Application.CARS.stream()
                    .map(CarController.this::addLink)
                    .collect(Collectors.toSet());
            this.add(linkTo(methodOn(CarController.class).cars()).withSelfRel());
        }

        CarList(Set<Car> carList, Integer eventId) {
            this.carList = carList.stream()
                    .map(car -> addLink(car, eventId))
                    .collect(Collectors.toSet());
            this.add(linkTo(methodOn(CarController.class).eventCars(eventId)).withSelfRel());
        }
    }

    @RequestMapping(value = "/v1/cars")
    @ApiOperation(
            value = "Lists all cars available",
            notes = "Returns a list of all cars available through the Project CARS Data API",
            response = Car.class,
            responseContainer = "List"
    )
    CarList cars() {
        return new CarList();
    }

    @RequestMapping(value = "/v1/cars/{carId}")
    @ApiOperation(
            value = "Returns a car",
            notes = "Returns a car as specified by its ID number",
            response = Car.class
    )
    Car cars(
            @ApiParam(
                    value = "ID of car to return",
                    required = true
            )
            @PathVariable Integer carId) {
        return Application.CARS.stream()
                .filter(car -> car.getCarId().equals(carId))
                .findAny()
                .map(this::addLink)
                .orElseThrow(() -> new CarNotFoundAPIException(carId));
    }

    @RequestMapping(value = "/v1/events/{eventId}/cars")
    @ApiOperation(
            value = "Lists all cars eligible for an event",
            notes = "Returns a list of all cars eligible for a single player event",
            response = Car.class,
            responseContainer = "List"
    )
    CarList eventCars(@PathVariable Integer eventId) {
        return Event.getEventByID(eventId)
                .map(event -> new CarList(event.getCars(), eventId))
                .orElseThrow(() -> new EventNotFoundAPIException(eventId));
    }

    @RequestMapping(value = "/v1/events/{eventId}/cars/{carId}")
    @ApiOperation(
            value = "Returns an eligible car for an event",
            notes = "Returns an eligible car for an event",
            response = Car.class
    )
    Car eventCars(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "ID of car",
                    required = true
            )
            @PathVariable Integer carId) {
        return Event.getEventByID(eventId).map(
                event -> event.getCars().stream()
                        .filter(car -> car.getCarId().equals(carId))
                        .findAny()
                        .map(car -> addLink(car, eventId))
                        .orElseThrow(() -> new CarNotFoundAPIException(carId)))
                .orElseThrow(() -> new EventNotFoundAPIException(eventId));
    }

    private Car addLink(Car car) {
        car.removeLinks();
        car.add(linkTo(methodOn(CarController.class).cars(car.getCarId())).withSelfRel());
        return car;
    }

    static Car addLink(Car car, Integer eventId) {
        car.removeLinks();
        car.add(linkTo(methodOn(CarController.class).eventCars(eventId, car.getCarId())).withSelfRel());
        car.add(linkTo(methodOn(CarController.class).cars(car.getCarId())).withRel("car"));
        return car;
    }
}
