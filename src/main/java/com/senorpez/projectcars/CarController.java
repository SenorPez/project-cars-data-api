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
        produces = "application/vnd.senorpez.pcars.v1+json; charset=UTF-8",
        tags = {"cars"})
@RequestMapping(
        value = "/cars",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class CarController {
    @ApiOperation(
            value = "Lists all cars available",
            notes = "Returns a list of all cars available through the Project CARS Data API",
            response = EmbeddedCar.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> cars() {
        IdentifiableResourceAssembler<EmbeddedCar, Resource> assembler = new IdentifiableResourceAssembler<>(CarController.class, Resource.class);
        return new Resources<>(
                Application.CARS.stream()
                        .map(EmbeddedCar::new)
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(CarController.class).cars()).withSelfRel(),
                linkTo(methodOn(RootController.class).root()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns a car",
            notes = "Returns a car as specified by its ID number",
            response = Car.class
    )
    @RequestMapping(value = "/{carId}")
    Resource cars(
            @ApiParam(
                    value = "ID of car to return",
                    required = true
            )
            @PathVariable Integer carId) {
        IdentifiableResourceAssembler<CarModel, Resource> assembler = new IdentifiableResourceAssembler<>(CarController.class, Resource.class);
        Car subjectCar = Application.CARS.stream()
                .filter(car -> car.getId().equals(carId))
                .findAny()
                .orElseThrow(() -> new CarNotFoundAPIException(carId));
        Resource resource = assembler.toResource(new CarModel(subjectCar));
        resource.add(linkTo(methodOn(CarController.class).cars()).withRel("cars"));
        resource.add(linkTo(methodOn(CarClassController.class).carClasses(subjectCar.getCarClass().getId())).withRel("class"));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return resource;
    }
}
