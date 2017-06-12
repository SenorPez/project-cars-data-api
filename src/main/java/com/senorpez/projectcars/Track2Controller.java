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

@Api(
        produces = "application/vnd.senorpez.pcars2.v0+json; charset=UTF-8",
        tags = {"cars"})
@RequestMapping(
        value = "/tracks",
        method = RequestMethod.GET,
        produces = "application/vnd.senorpez.pcars2.v0+json; charset=UTF-8")
@RestController
class Track2Controller {
    @ApiOperation(
            value = "List all tracks available",
            notes = "Returns a list of all tracks available through the Project CARS 2 Data API",
            response = Track2.class,
            responseContainer = "List")
    @RequestMapping
    Resources<Resource> tracks() {
        IdentifiableResourceAssembler<Track2, Resource> assembler = new IdentifiableResourceAssembler<>(Track2Controller.class, Resource.class);
        return new Resources<>(
                Application.TRACKS2.stream()
                        .map(assembler::toResource)
                        .collect(Collectors.toList()),
                linkTo(methodOn(Track2Controller.class).tracks()).withSelfRel(),
                linkTo(methodOn(RootController.class).root2()).withRel("index"));
    }

    @ApiOperation(
            value = "Returns a track",
            notes = "Return a track as specified by its ID number",
            response = Track2.class)
    @RequestMapping(value = "/{trackId}")
    Resource tracks(
            @ApiParam(
                    value = "ID of track to return",
                    required = true)
            @PathVariable final Integer trackId) {
        IdentifiableResourceAssembler<Track2, Resource> assembler = new IdentifiableResourceAssembler<>(Track2.class, Resource.class);
        Resource resource = assembler.toResource(Application.TRACKS2.parallelStream()
                .filter(track -> track.getId().equals(trackId))
                .findAny()
                .orElseThrow(() -> new TrackNotFoundAPIException(trackId)));
        resource.add(linkTo(methodOn(Track2Controller.class).tracks()).withRel("tracks"));
        resource.add(linkTo(methodOn(RootController.class).root2()).withRel("index"));
        return resource;
    }
}
