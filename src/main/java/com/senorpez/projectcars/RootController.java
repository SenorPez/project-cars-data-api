package com.senorpez.projectcars;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping(method = {RequestMethod.GET})
class RootController {
    @RequestMapping(value = "/v1")
    ResourceSupport root() {
        ResourceSupport root = new ResourceSupport();
        root.add(linkTo(methodOn(RootController.class).root()).withSelfRel());
        root.add(linkTo(methodOn(CarController.class).cars()).withRel("cars"));
        root.add(linkTo(methodOn(EventController.class).events()).withRel("events"));
        root.add(linkTo(methodOn(TrackController.class).tracks()).withRel("tracks"));

        return root;
    }
}
