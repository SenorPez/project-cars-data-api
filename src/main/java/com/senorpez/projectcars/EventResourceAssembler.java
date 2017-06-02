package com.senorpez.projectcars;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import java.util.ArrayList;
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
        List<Resource> embeds = new ArrayList<>();

        IdentifiableResourceAssembler<EmbeddedCar, Resource> carAssembler = new IdentifiableResourceAssembler<>(CarController.class, Resource.class);
        embeds.addAll(entity.getCars().stream()
                .map(EmbeddedCar::new)
                .map(carAssembler::toResource)
                .collect(Collectors.toList()));

        EmbeddedRoundResourceAssembler roundAssembler = new EmbeddedRoundResourceAssembler(entity.getId());
        embeds.addAll(entity.getRounds().stream()
                .map(EmbeddedRound::new)
                .map(roundAssembler::toResource)
                .collect(Collectors.toList()));

        return new EventResource(entity, embeds);
    }
}
