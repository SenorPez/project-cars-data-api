package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

@Relation(value = "track", collectionRelation = "track")
class EmbeddedTrack implements Identifiable<Integer> {
    @JsonProperty("id")
    private final Integer id;
    @JsonProperty("name")
    private final String name;

    EmbeddedTrack(Track track) {
        this.id = track.getId();
        this.name = track.getName();
    }

    @Override
    public Integer getId() {
        return id;
    }
}
