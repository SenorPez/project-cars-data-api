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
@Api(tags = {"events"})
@RequestMapping(method = {RequestMethod.GET})
class EventController {
    class EventList extends ResourceSupport {
        @JsonProperty("events")
        private final Set<Event> eventList;

        EventList() {
            this.eventList = Application.EVENTS.stream()
                    .map(EventController.this::addLink)
                    .collect(Collectors.toSet());
            this.add(linkTo(methodOn(EventController.class).events()).withSelfRel());
        }
    }

    @RequestMapping(value = "/v1/events")
    @ApiOperation(
            value = "Lists all events available",
            notes = "Returns a list of all single player events available through the Project CARS Data API",
            response = Event.class,
            responseContainer = "List"
    )
    EventList events() {
        return new EventList();
    }

    @RequestMapping(value = "/v1/events/{eventId}")
    @ApiOperation(
            value = "Returns an event",
            notes = "Returns an event as specified by its ID number",
            response = Event.class
    )
    Event events(
            @ApiParam(
                    value = "ID of event to return",
                    required = true
            )
            @PathVariable Integer eventId) {
        return Application.EVENTS.stream()
                .filter(event -> event.getEventId().equals(eventId))
                .findAny()
                .map(this::addLink)
                .orElse(null);
    }

    private Event addLink(Event event) {
        event.removeLinks();
        event.add(linkTo(methodOn(EventController.class).events(event.getEventId())).withSelfRel());
        event.add(linkTo(methodOn(CarController.class).eventCars(event.getEventId())).withRel("cars"));
        event.getCars().forEach(car -> CarController.addLink(car, event.getEventId()));
        event.getRounds().forEach(round -> RoundController.addLink(round, event.getEventId()));
        return event;
    }
}
