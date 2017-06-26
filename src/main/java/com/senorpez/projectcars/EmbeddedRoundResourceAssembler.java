package com.senorpez.projectcars;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

class EmbeddedRoundResourceAssembler extends IdentifiableResourceAssemblerSupport<EmbeddedRound, Resource> {
    private final Integer eventId;

    EmbeddedRoundResourceAssembler(Integer eventId) {
        super(RoundController.class, Resource.class);
        this.eventId = eventId;
    }

    @Override
    public Resource toResource(EmbeddedRound entity) {
        return createResource(entity, eventId, entity.getId());
    }

    @Override
    protected Resource instantiateResource(EmbeddedRound entity) {
        return new Resource<>(entity);
    }
}
