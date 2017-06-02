package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

class EventResource extends Resources<Resource> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("tier")
    private final Integer tier;
    @JsonProperty("verified")
    private final Boolean verified;

    EventResource(Event event, Iterable<Resource> content, Link... links) {
        super(content, links);
        this.id = event.getId();
        this.name = event.getName();
        this.tier = event.getTier();
        this.verified = event.isVerified();
    }
}