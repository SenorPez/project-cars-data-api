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
@Api(tags = {"events"})
@RequestMapping(method = {RequestMethod.GET})
class EventController {
    class EventList extends ResourceSupport {
        @JsonProperty("events")
        private final Set<Event> eventList;

        EventList(Set<Event> eventList) {
            this.eventList = eventList;
        }
    }

    @RequestMapping(value = "/v1/events")
    @ApiOperation(
            value = "Lists all events available",
            notes = "Returns a list of all single player events available through the Project CARS Data API",
            response = Event.class,
            responseContainer = "List"
    )
    public EventList events() {
        return new EventList(Application.EVENTS);
    }

    @RequestMapping(value = "/v1/events/{eventId}")
    @ApiOperation(
            value = "Returns an event",
            notes = "Returns an event as specified by its ID number",
            response = Event.class
    )
    public Event events(
            @ApiParam(
                    value = "ID of event to return",
                    required = true
            )
            @PathVariable Integer eventId) {
        return Application.EVENTS.stream()
                .filter(event -> event.getEventId().equals(eventId))
                .findAny()
                .orElse(null);
    }
}
