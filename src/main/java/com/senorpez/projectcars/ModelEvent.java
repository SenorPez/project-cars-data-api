package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;

class ModelEvent implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("tier")
    private final Integer tier;
    @JsonProperty("verified")
    private final Boolean verified;

    ModelEvent(Event event) {
        this.id = event.getId();
        this.name = event.getName();
        this.tier = event.getTier();
        this.verified = event.isVerified();
    }

    @Override
    public Integer getId() {
        return id;
    }
}
