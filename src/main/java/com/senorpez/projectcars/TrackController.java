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

@Api(tags = {"tracks"})
@RequestMapping(
        value = "/tracks",
        method = {RequestMethod.GET},
produces = {"application/json; charset=UTF-8", "application/vnd.senorpez.pcars.v1+json; charset=UTF-8"}
)
@RestController
class TrackController {
    @ApiOperation(
            value = "Lists all tracks available",
            notes = "Returns a list of all tracks available through the Project CARS Data API",
            response = EmbeddedTrack.class,
            responseContainer = "List"
    )
    @RequestMapping
    Resources<Resource> tracks() {
        IdentifiableResourceAssembler<EmbeddedTrack, Resource> assembler = new IdentifiableResourceAssembler<>(TrackController.class, Resource.class);
        return new Resources<>(
                Application.TRACKS.stream()
                        .map(EmbeddedTrack::new)
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(TrackController.class).tracks()).withSelfRel());
    }

        @ApiOperation(
            value = "Returns a track",
            notes = "Returns a track as specified by its ID number",
            response = Track.class
    )
    @RequestMapping(value = "/{trackId}")
    Resource tracks(
            @ApiParam(
                    value = "ID of track to return",
                    required = true
            )
            @PathVariable Integer trackId) {
            IdentifiableResourceAssembler<Track, Resource> assembler = new IdentifiableResourceAssembler<>(TrackController.class, Resource.class);
            return assembler.toResource(Application.TRACKS.stream()
                    .filter(track -> track.getId().equals(trackId))
                    .findAny()
                    .orElseThrow(() -> new TrackNotFoundAPIException(trackId)));
    }
}
