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

@Api(tags = {"rounds"})
@RequestMapping(
        value = "/events/{eventId}/rounds",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class RoundController {
    @ApiOperation(
            value = "Lists all rounds available for an event",
            notes = "Returns a list of all rounds in a single player event",
            response = EmbeddedRound.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> rounds(@PathVariable Integer eventId) {
        EmbeddedRoundResourceAssembler assembler = new EmbeddedRoundResourceAssembler(eventId);
        return new Resources<>(
                Application.EVENTS.stream()
                        .filter(event -> event.getId().equals(eventId))
                        .findAny()
                        .orElseThrow(() -> new EventNotFoundAPIException(eventId))
                        .getRounds().stream()
                        .map(EmbeddedRound::new)
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(RoundController.class).rounds(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).events(eventId)).withRel("event"),
                linkTo(methodOn(RootController.class).root()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns a round",
            notes = "Returns a round as specified by event ID and round number",
            response = Car.class
    )
    @RequestMapping(value = "/{roundId}")
    RoundResource rounds(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "Round number",
                    required = true
            )
            @PathVariable Integer roundId) {
        RoundResourceAssembler assembler = new RoundResourceAssembler(eventId, roundId);
        RoundResource resource = assembler.toResource(Application.EVENTS.stream()
                .filter(event -> event.getId().equals(eventId))
                .findAny()
                .orElseThrow(() -> new EventNotFoundAPIException(eventId))
                .getRounds().stream()
                .filter(round -> round.getId().equals(roundId))
                .findAny()
                .orElseThrow(() -> new RoundNotFoundAPIException(roundId)));
        resource.add(linkTo(methodOn(RoundController.class).rounds(eventId)).withRel("rounds"));
        resource.add(linkTo(methodOn(RaceController.class).races(eventId, roundId)).withRel("races"));
        resource.add(linkTo(methodOn(RootController.class).root()).withRel("index"));
        return resource;
    }
}
