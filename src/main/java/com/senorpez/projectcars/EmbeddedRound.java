package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "round", collectionRelation = "round")
class EmbeddedRound implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;

    EmbeddedRound(Round round) {
        this.id = round.getId();
    }

    @Override
    public Integer getId() {
        return id;
    }
}
