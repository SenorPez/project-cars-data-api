package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "livery", collectionRelation = "livery")
class Livery implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;

    @JsonCreator
    public Livery(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
