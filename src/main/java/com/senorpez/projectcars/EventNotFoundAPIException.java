package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class EventNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    EventNotFoundAPIException(Integer eventId) {
        this.code = status.toString() + "-events-" + eventId.toString();
        this.messageApi = String.format("Event with ID of %d not found", eventId);
    }
}
