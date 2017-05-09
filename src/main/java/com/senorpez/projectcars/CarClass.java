package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

class CarClass extends ResourceSupport{
    @JsonProperty("id")
    private final Integer carClassId;
    @JsonProperty("name")
    private final String name;

    @JsonCreator
    public CarClass(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name) {
        this.carClassId = id;
        this.name = name;

        this.add(new Link(String.format("/classes/%s", carClassId.toString())).withSelfRel());
    }

    public Integer getCarClassId() {
        return carClassId;
    }
}
