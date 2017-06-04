package com.senorpez.projectcars;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RequestMapping(
        value = "/",
        method = {RequestMethod.GET},
        produces = {"application/vnd.senorpez.pcars.v1+json; charset=UTF-8", "application/json; charset=UTF-8"}
)
@RestController
class RootController {
    @RequestMapping
    ResourceSupport root() {
        ResourceSupport root = new ResourceSupport();
        root.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        root.add(linkTo(methodOn(CarController.class).cars()).withRel("cars"));
        root.add(linkTo(methodOn(CarClassController.class).carClasses()).withRel("classes"));
        root.add(linkTo(methodOn(EventController.class).events()).withRel("events"));
        root.add(linkTo(methodOn(TrackController.class).tracks()).withRel("tracks"));
        return root;
    }
}
