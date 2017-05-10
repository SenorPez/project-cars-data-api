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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(tags = {"races"})
@RequestMapping(method = {RequestMethod.GET})
class RaceController {
    class RaceList extends ResourceSupport {
        @JsonProperty("races")
        private final Set<Race> raceList;

        RaceList(Set<Race> raceList, Integer eventId, Integer roundId) {
            this.raceList = raceList.stream()
                    .map(race -> addLink(race, eventId, roundId))
                    .collect(Collectors.toSet());
            this.add(linkTo(methodOn(RaceController.class).races(eventId, roundId)).withSelfRel());
        }
    }

    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}/races")
    @ApiOperation(
            value = "Returns all races available for a round",
            notes = "Returns a list of all races in a round of a single player event.",
            response = Round.class
    )
    public RaceList races(
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
        return Round.getRoundById(eventID, roundID).map(
                round -> new RaceList(round.getRaces(), eventID, roundID))
                .orElse(null);
    }

    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}/races/{raceID}")
    @ApiOperation(
            value = "Returns all races available for a round",
            notes = "Returns a list of all races in a round of a single player event.",
            response = Round.class
    )
    public Race races(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventID,
            @ApiParam(
                    value = "Round number within event.",
                    required = true
            )
            @PathVariable Integer roundID,
            @ApiParam(
                    value = "Race number within round.",
                    required = true
            )
            @PathVariable Integer raceID) {
        return Round.getRoundById(eventID, roundID).map(
                round -> round.getRaces().stream()
                        .filter(race -> race.getRaceId().equals(raceID))
                        .findAny()
                        .map(race -> addLink(race, eventID, roundID))
                        .orElse(null))
                .orElse(null);
    }

    static Race addLink(Race race, Integer eventId, Integer roundId) {
        race.removeLinks();
        race.add(linkTo(methodOn(RaceController.class).races(eventId, roundId, race.getRaceId())).withSelfRel());
        race.add(linkTo(methodOn(RoundController.class).rounds(eventId, roundId)).withRel("round"));
        race.add(linkTo(methodOn(EventController.class).events(eventId)).withRel("event"));
        return race;
    }
}
