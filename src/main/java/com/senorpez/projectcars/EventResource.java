package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;

import java.util.ArrayList;
import java.util.List;

class EventResource extends ResourceSupport {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("tier")
    private final Integer tier;
    @JsonProperty("verified")
    private final Boolean verified;

    @JsonUnwrapped
    private final Resources<EmbeddedWrapper> embeds;

    EventResource(Event event, Resources<EmbeddedWrapper> embeds) {
        this.id = event.getId();
        this.name = event.getName();
        this.tier = event.getTier();
        this.verified = event.isVerified();

        this.embeds = embeds;
    }
}
