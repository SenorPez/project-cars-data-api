package com.senorpez.projectcars;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Api(
        produces = "application/vnd.senorpez.pcars2.v0+json; charset=UTF-8",
        tags = {"cars"})
@RequestMapping(
        value = "/cars",
        method = RequestMethod.GET,
        produces = "application/vnd.senorpez.pcars2.v0+json; charset=UTF-8")
@RestController
class Car2Controller {
    @ApiOperation(
            value = "Lists all cars available",
            notes = "Returns a list of all cars available through the Project CARS 2 Data API",
            response = Car2.class,
            responseContainer = "List")
    @RequestMapping
    Resources<Resource> cars() {
        IdentifiableResourceAssembler<Car2, Resource> assembler = new IdentifiableResourceAssembler<>(Car2Controller.class, Resource.class);
        return new Resources<>(
                Application.CARS2.stream()
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(Car2Controller.class).cars()).withSelfRel(),
                linkTo(methodOn(RootController.class).root2()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns a car",
            notes = "Returns a car as specified by its ID number",
            response = Car2.class)
    @RequestMapping(value = "/{carId}")
    Resource cars(
            @ApiParam(
                    value = "ID of car to return",
                    required = true)
            @PathVariable final Integer carId) {
        IdentifiableResourceAssembler<Car2, Resource> assembler = new IdentifiableResourceAssembler<>(Car2Controller.class, Resource.class);
        Car2 subjectCar = Application.CARS2.parallelStream()
                .filter(car -> car.getId().equals(carId))
                .findAny()
                .orElseThrow(() -> new CarNotFoundAPIException(carId));
        Resource resource = assembler.toResource(subjectCar);
        resource.add(linkTo(methodOn(Car2Controller.class).cars()).withRel("cars"));
        resource.add(linkTo(methodOn(RootController.class).root2()).withRel("index"));
        return resource;
    }
}

