//package com.senorpez.projectcars;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.springframework.hateoas.ResourceSupport;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
//
//@RestController
//@Api(tags = {"tracks"})
//@RequestMapping(method = {RequestMethod.GET})
//class TrackController {
//    private class TrackList extends ResourceSupport {
//        @JsonProperty("tracks")
//        private final Set<Track> trackList;
//
//        TrackList() {
//            this.trackList = Application.TRACKS.stream()
//                    .map(TrackController::addLink)
//                    .collect(Collectors.toSet());
//            this.add(linkTo(methodOn(TrackController.class).tracks()).withSelfRel());
//        }
//    }
//
//    @RequestMapping(value = "/v1/tracks")
//    @ApiOperation(
//            value = "Lists all tracks",
//            notes = "Returns a list of all tracks available through the Project CARS Data API",
//            response = Track.class,
//            responseContainer = "List"
//    )
//    TrackList tracks() {
//        return new TrackList();
//    }
//
//    @RequestMapping(value = "/v1/tracks/{trackId}")
//    @ApiOperation(
//            value = "Lists all tracks",
//            notes = "Returns a track as specified by its ID number",
//            response = Track.class
//    )
//    Track tracks(
//            @ApiParam(
//                    value = "ID of track to return",
//                    required = true
//            )
//            @PathVariable Integer trackId) {
//        return Application.TRACKS.stream()
//                .filter(track -> track.getId().equals(trackId))
//                .findAny()
//                .map(TrackController::addLink)
//                .orElseThrow(() -> new TrackNotFoundAPIException(trackId));
//    }
//
//    static Track addLink(Track track) {
//        track.removeLinks();
//        track.add(linkTo(methodOn(TrackController.class).tracks(track.getId())).withSelfRel());
//        return track;
//    }
//}
