package com.senorpez.projectcars;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class RoundResourceAssembler extends IdentifiableResourceAssemblerSupport<Round, RoundResource> {
    private final Integer eventId;
    private final Integer roundId;

    RoundResourceAssembler(Integer eventId, Integer roundId) {
        super(RoundController.class, RoundResource.class);
        this.eventId = eventId;
        this.roundId = roundId;
    }

    @Override
    public RoundResource toResource(Round entity) {
        return createResource(entity, eventId, roundId);
    }

    @Override
    protected RoundResource instantiateResource(Round entity) {
        List<Resource> embeds = new ArrayList<>();

        IdentifiableResourceAssembler<EmbeddedTrack, Resource> trackAssembler = new IdentifiableResourceAssembler<>(TrackController.class, Resource.class);

        Resource resource = trackAssembler.toResource(new EmbeddedTrack(entity.getTrack()));
        resource.add(linkTo(methodOn(RoundController.class).roundTrack(eventId, roundId)).withSelfRel());
        embeds.add(resource);

        return new RoundResource(entity, embeds);
    }
}
