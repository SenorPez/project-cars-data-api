package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

class RoundResource extends Resources<Resource> {
    @JsonProperty("id")
    private final Integer id;

    RoundResource(Round round, Iterable<Resource> content, Link... links) {
        super(content, links);
        this.id = round.getId();
    }
}
