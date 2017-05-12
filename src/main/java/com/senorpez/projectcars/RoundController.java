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
@Api(tags = {"rounds"})
@RequestMapping(method = {RequestMethod.GET})
class RoundController {
    class RoundList extends ResourceSupport {
        @JsonProperty("rounds")
        private final Set<Round> roundList;

        RoundList(Set<Round> roundList, Integer eventId) {
            this.roundList = roundList.stream()
                    .map(round -> addLink(round, eventId))
                    .collect(Collectors.toSet());
            this.add(linkTo(methodOn(RoundController.class).rounds(eventId)).withSelfRel());
        }
    }

    @RequestMapping(value = "/v1/events/{eventId}/rounds")
    @ApiOperation(
            value = "Lists all rounds available for an event",
            notes = "Returns a list of all rounds in a single player event",
            response = Round.class,
            responseContainer = "List"
    )
    RoundList rounds(@PathVariable Integer eventId) {
        return Event.getEventByID(eventId).map(
                event -> new RoundList(event.getRounds(), eventId))
                .orElseThrow(() -> new EventNotFoundAPIException(eventId));
    }

    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}")
    @ApiOperation(
            value = "Returns a round for an event",
            notes = "Returns a specific round for an event.",
            response = Round.class
    )
    Round rounds(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventID,
            @ApiParam(
                    value = "Round number within event.",
                    required = true
            )
            @PathVariable Integer roundID) {
        return Event.getEventByID(eventID).map(
                event -> event.getRounds().stream()
                        .filter(round -> round.getRoundId().equals(roundID))
                        .findAny()
                        .map(round -> addLink(round, eventID))
                        .orElseThrow(() -> new RoundNotFoundAPIException(roundID)))
                .orElseThrow(() -> new EventNotFoundAPIException(eventID));
    }

    static Round addLink(Round round, Integer eventId) {
        round.removeLinks();
        round.add(linkTo(methodOn(RoundController.class).rounds(eventId, round.getRoundId())).withSelfRel());
        round.add(linkTo(methodOn(EventController.class).events(eventId)).withRel("event"));
        round.add(linkTo(methodOn(TrackController.class).tracks(round.getTrack().getTrackId())).withRel("track"));
        round.getRaces().forEach(race -> RaceController.addLink(race, eventId, round.getRoundId()));
        TrackController.addLink(round.getTrack());
        return round;
    }
}
