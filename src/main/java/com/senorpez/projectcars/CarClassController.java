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

@Api(tags = {"classes"})
@RequestMapping(
        value = "/classes",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class CarClassController {
    @ApiOperation(
            value = "Lists all car classes available",
            notes = "Returns a list of all car classes available through the Project CARS Data API",
            response = CarClass.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> carClasses() {
        IdentifiableResourceAssembler<CarClass, Resource> assembler = new IdentifiableResourceAssembler<>(CarClassController.class, Resource.class);
        return new Resources<>(
                Application.CAR_CLASSES.stream()
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(CarClassController.class).carClasses()).withSelfRel(),
                linkTo(methodOn(RootController.class).root()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns a car class",
            notes = "Returns a car class as specified by its ID number",
            response = CarClass.class
    )
    @RequestMapping(value = "/{carClassId}")
    Resource carClasses(
            @ApiParam(
                    value = "ID of car class to return",
                    required = true
            )
            @PathVariable Integer carClassId) {
        IdentifiableResourceAssembler<CarClass, Resource> assembler = new IdentifiableResourceAssembler<>(CarClassController.class, Resource.class);
        Resource resource = assembler.toResource(Application.CAR_CLASSES.stream()
                .filter(carClass -> carClass.getId().equals(carClassId))
                .findAny()
                .orElseThrow(() -> new CarClassNotFoundAPIException(carClassId)));
        resource.add(linkTo(methodOn(CarClassController.class).carClasses()).withRel("classes"));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return resource;
    }
}