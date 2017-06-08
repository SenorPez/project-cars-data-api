package com.senorpez.projectcars;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.IdentifiableResourceAssemblerSupport;

import java.util.ArrayList;
import java.util.List;

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
        embeds.add(trackAssembler.toResource(new EmbeddedTrack(entity.getTrack())));

        return new RoundResource(entity, embeds);
    }
}
