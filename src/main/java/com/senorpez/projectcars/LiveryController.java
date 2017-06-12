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

@Api(tags = {"liveries"})
@RequestMapping(
        value = "/cars/{carId}/liveries",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class LiveryController {
    @ApiOperation(
            value = "Lists all liveries available for a car",
            notes = "Returns a list of all liveries for a car",
            response = Livery.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> liveries(
            @ApiParam(
                    value = "ID of car to return liveries",
                    required = true
            )
            @PathVariable Integer carId) {
        LiveryResourceAssembler assembler = new LiveryResourceAssembler(carId);
        return new Resources<>(
                Application.CARS.stream()
                        .filter(car -> car.getId().equals(carId))
                        .findAny()
                        .orElseThrow(() -> new CarNotFoundAPIException(carId))
                        .getLiveries().stream()
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(LiveryController.class).liveries(carId)).withSelfRel(),
                linkTo(methodOn(RootController.class).root()).withRel("index"),
                linkTo(methodOn(CarController.class).cars(carId)).withRel("car")
        );
    }

    @ApiOperation(
            value = "Lists a livery available for a car",
            notes = "Returns a livery for a car",
            response = Livery.class
    )
    @RequestMapping(value = "/{liveryId}")
    Resource liveries(
            @ApiParam(
                    value = "ID of car to return a livery from",
                    required = true
            )
            @PathVariable Integer carId,
            @ApiParam(
                    value = "ID of livery to return",
                    required = true
            )
            @PathVariable Integer liveryId) {
        LiveryResourceAssembler assembler = new LiveryResourceAssembler(carId);
        Resource resource = assembler.toResource(Application.CARS.stream()
                .filter(car -> car.getId().equals(carId))
                .findAny()
                .orElseThrow(() -> new CarNotFoundAPIException(carId))
                .getLiveries().stream()
                .filter(livery -> livery.getId().equals(liveryId))
                .findAny()
                .orElseThrow(() -> new LiveryNotFoundAPIException(liveryId)));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        resource.add(linkTo(methodOn(LiveryController.class).liveries(carId)).withRel("liveries"));
        return resource;
    }
}
