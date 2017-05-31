package com.senorpez.projectcars;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.core.EmbeddedWrapper;
import org.springframework.hateoas.core.EmbeddedWrappers;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import java.util.List;
import java.util.stream.Collectors;

class EventResourceAssembler extends IdentifiableResourceAssemblerSupport<Event, EventResource> {
    EventResourceAssembler() {
        super(EventController.class, EventResource.class);
    }

    @Override
    public EventResource toResource(Event entity) {
        return createResource(entity);
    }

    @Override
    protected EventResource instantiateResource(Event entity) {
        EmbeddedWrappers wrappers = new EmbeddedWrappers(true);
        IdentifiableResourceAssembler<EmbeddedCar, Resource> carAssembler = new IdentifiableResourceAssembler<>(CarController.class, Resource.class);
        List<EmbeddedWrapper> cars = entity.getCars().stream()
                .map(EmbeddedCar::new)
                .map(carAssembler::toResource)
                .map(wrappers::wrap)
                .collect(Collectors.toList());

        Resources<EmbeddedWrapper> embeddedCars = new Resources<>(cars);
        return new EventResource(entity, embeddedCars);

//            Resources<Resource> cars = new Resources<>(
//                    entity.getCars().stream()
//                            .map(EmbeddedCar::new)
//                            .map(carAssembler::toResource)
//                            .collect(Collectors.toList()),
//                    new Link("http://example.org").withRel("test"));
//            return new EventResource(entity, cars);
    }
}
