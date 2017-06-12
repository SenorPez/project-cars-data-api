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

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Api(tags = {"events"})
@RequestMapping(
        value = "/events",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class EventController {
    @ApiOperation(
            value = "Lists all events available",
            notes = "Returns a list of all single player events available through the Project CARS Data API",
            response = EmbeddedEvent.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> events() {
        IdentifiableResourceAssembler<EmbeddedEvent, Resource> assembler = new IdentifiableResourceAssembler<>(EventController.class, Resource.class);
        return new Resources<>(
                Application.EVENTS.stream()
                        .map(EmbeddedEvent::new)
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(EventController.class).events()).withSelfRel(),
                linkTo(methodOn(RootController.class).root()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns an event",
            notes = "Returns an event as specified by its ID number",
            response = Event.class
    )
    @RequestMapping(value = "/{eventId}")
    Resource events(
            @ApiParam(
                    value = "ID of event to return",
                    required = true
            )
            @PathVariable Integer eventId) {
        IdentifiableResourceAssembler<EventModel, Resource> assembler = new IdentifiableResourceAssembler<>(EventController.class, Resource.class);
        Resource resource = assembler.toResource(Application.EVENTS.stream()
                .filter(event -> event.getId().equals(eventId))
                .findAny()
                .map(EventModel::new)
                .orElseThrow(() -> new EventNotFoundAPIException(eventId)));
        resource.add(linkTo(methodOn(EventController.class).events()).withRel("events"));
        resource.add(linkTo(methodOn(EventController.class).eventCars(eventId)).withRel("cars"));
        resource.add(linkTo(methodOn(RoundController.class).rounds(eventId)).withRel("rounds"));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return resource;
    }

    @ApiOperation(
            value = "Lists all cars available for an event",
            notes = "Returns a list of all cars in a single player event",
            response = EmbeddedCar.class,
            responseContainer = "List"
    )
    @RequestMapping(value = "/{eventId}/cars")
    Resources<Resource> eventCars(
            @ApiParam(
                    value = "ID of event to return cars",
                    required = true
            )
            @PathVariable Integer eventId) {
        IdentifiableResourceAssembler<EmbeddedCar, Resource> assembler = new IdentifiableResourceAssembler<>(CarController.class, Resource.class);
        return new Resources<>(
                Application.EVENTS.stream()
                        .filter(event -> event.getId().equals(eventId))
                        .findAny()
                        .orElseThrow(() -> new EventNotFoundAPIException(eventId))
                        .getCars().stream()
                        .map(EmbeddedCar::new)
                        .map(assembler::toResource)
                        .map(car -> {
                            EmbeddedCar embeddedCar = (EmbeddedCar) car.getContent();
                            car.add(linkTo(methodOn(EventController.class).eventCars(eventId, embeddedCar.getId())).withSelfRel());
                            return car;
                        })
                        .collect(Collectors.toList()),
                linkTo(methodOn(EventController.class).eventCars(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).events(eventId)).withRel("event"),
                linkTo(methodOn(RootController.class).root()).withRel("index"));
    }

    @ApiOperation(
            value = "Lists a car available for an event",
            notes = "Returns a car in a single player event",
            response = Car.class
    )
    @RequestMapping(value = "/{eventId}/cars/{carId}")
    Resource eventCars(
            @ApiParam(
                    value = "ID of event to return cars",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "ID of car to return",
                    required = true
            )
            @PathVariable Integer carId) {
        IdentifiableResourceAssembler<CarModel, Resource> assembler = new IdentifiableResourceAssembler<>(CarController.class, Resource.class);
        Car subjectCar = Application.EVENTS.stream()
                .filter(event -> event.getId().equals(eventId))
                .findAny()
                .orElseThrow(() -> new EventNotFoundAPIException(eventId))
                .getCars().stream()
                .filter(car -> car.getId().equals(carId))
                .findAny()
                .orElseThrow(() -> new CarNotFoundAPIException(carId));
        Resource resource = assembler.toResource(new CarModel(subjectCar));
        resource.add(linkTo(methodOn(EventController.class).eventCars(eventId, carId)).withSelfRel());
        resource.add(linkTo(methodOn(EventController.class).eventCars(eventId)).withRel("cars"));
        resource.add(linkTo(methodOn(CarClassController.class).carClasses(subjectCar.getCarClass().getId())).withRel("class"));
        resource.add(linkTo(methodOn(LiveryController.class).liveries(carId)).withRel("liveries"));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return resource;
    }
}