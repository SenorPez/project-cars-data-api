package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "event", collectionRelation = "event")
class EmbeddedEvent implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;

    public EmbeddedEvent(Event event) {
        this.id = event.getId();
        this.name = event.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }
}
