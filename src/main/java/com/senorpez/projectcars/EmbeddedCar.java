package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "car", collectionRelation = "cars")
class EmbeddedCar implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("carName")
    private final String carName;

    EmbeddedCar(Car car) {
        this.id = car.getId();
        this.carName = String.join(" ", car.getManufacturer(), car.getModel());
    }

    @Override
    public Integer getId() {
        return id;
    }
}