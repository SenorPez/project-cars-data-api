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

@Api(tags = {"races"})
@RequestMapping(
        value = "/events/{eventId}/rounds/{roundId}/races",
        method = {RequestMethod.GET},
        produces = {"application/json; charset=UTF-8", "application/vnd.senorpez.pcars.v1+json; charset=UTF-8"}
)
@RestController
class RaceController {
    @ApiOperation(
            value = "Lists all races available for an event round",
            notes = "Returns a list of all races in a round of a single player event",
            response = Race.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> races(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "Round number within event.",
                    required = true
            )
            @PathVariable Integer roundId) {
        RaceResourceAssembler assembler = new RaceResourceAssembler(eventId, roundId);
        return new Resources<>(
                Application.EVENTS.stream()
                        .filter(event -> event.getId().equals(eventId))
                        .findAny()
                        .orElseThrow(() -> new EventNotFoundAPIException(eventId))
                        .getRounds().stream()
                        .filter(round -> round.getId().equals(roundId))
                        .findAny()
                        .orElseThrow(() -> new RoundNotFoundAPIException(roundId))
                        .getRaces().stream()
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(RaceController.class).races(eventId, roundId)).withSelfRel(),
                linkTo(methodOn(RoundController.class).rounds(eventId, roundId)).withRel("round"),
                linkTo(methodOn(RootController.class).root()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns a race available for an event round",
            notes = "Returns a race in a round of a single player event",
            response = Round.class
    )
    @RequestMapping(value = "/{raceId}")
    Resource races(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "Round number within event.",
                    required = true
            )
            @PathVariable Integer roundId,
            @ApiParam(
                    value = "Race number within round.",
                    required = true
            )
            @PathVariable Integer raceId) {
        RaceResourceAssembler assembler = new RaceResourceAssembler(eventId, roundId);
        Resource resource = assembler.toResource(Application.EVENTS.stream()
                .filter(event -> event.getId().equals(eventId))
                .findAny()
                .orElseThrow(() -> new EventNotFoundAPIException(eventId))
                .getRounds().stream()
                .filter(round -> round.getId().equals(roundId))
                .findAny()
                .orElseThrow(() -> new RoundNotFoundAPIException(roundId))
                .getRaces().stream()
                .filter(race -> race.getId().equals(raceId))
                .findAny()
                .orElseThrow(() -> new RaceNotFoundAPIException(raceId)));
        resource.add(linkTo(methodOn(RaceController.class).races(eventId, roundId)).withRel("races"));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return resource;
    }
}
