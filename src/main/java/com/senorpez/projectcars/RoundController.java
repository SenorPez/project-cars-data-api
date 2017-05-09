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

@RestController
@Api(tags = {"rounds"})
@RequestMapping(method = {RequestMethod.GET})
class RoundController {
    class RoundList extends ResourceSupport {
        @JsonProperty("rounds")
        private final List<Round> roundList;

        public RoundList(List<Round> roundList) {
            this.roundList = roundList;
        }
    }

    @RequestMapping(value = "/v1/events/{eventId}/rounds")
    @ApiOperation(
            value = "Lists all rounds available for an event",
            notes = "Returns a list of all rounds in a single player event",
            response = Round.class,
            responseContainer = "List"
    )
    public RoundList rounds(@PathVariable Integer eventId) {
        return Event.getEventByID(eventId).map(
                event -> new RoundList(event.getRounds()))
                .orElse(null);
    }

    @RequestMapping(value = "/v1/events/{eventID}/rounds/{roundID}")
    @ApiOperation(
            value = "Returns a round for an event",
            notes = "Returns a specific round for an event.",
            response = Round.class
    )
    public Round rounds(
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
                        .orElse(null))
                .orElse(null);
    }
}
