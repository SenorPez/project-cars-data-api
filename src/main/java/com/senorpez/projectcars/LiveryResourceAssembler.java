package com.senorpez.projectcars;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

class LiveryResourceAssembler extends IdentifiableResourceAssemblerSupport<Livery, Resource> {
    private final Integer carId;

    LiveryResourceAssembler(Integer carId) {
        super(LiveryController.class, Resource.class);
        this.carId = carId;
    }

    @Override
    public Resource toResource(Livery entity) {
        return createResource(entity, carId, entity.getId());
    }

    @Override
    protected Resource instantiateResource(Livery entity) {
        return new Resource<>(entity);
    }
}
