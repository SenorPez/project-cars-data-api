package com.senorpez.projectcars;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

class RaceResourceAssembler extends IdentifiableResourceAssemblerSupport<Race, Resource> {
    private final Integer eventId;
    private final Integer roundId;

    RaceResourceAssembler(Integer eventId, Integer roundId) {
        super(RaceController.class, Resource.class);
        this.eventId = eventId;
        this.roundId = roundId;
    }

    @Override
    public Resource toResource(Race entity) {
        return createResource(entity, eventId, roundId, entity.getId());
    }

    @Override
    protected Resource instantiateResource(Race entity) {
        return new Resource<>(entity);
    }
}
