package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "car", collectionRelation = "car")
class EmbeddedCar implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;

    EmbeddedCar(Car car) {
        this.id = car.getId();
        this.name = car.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }
}