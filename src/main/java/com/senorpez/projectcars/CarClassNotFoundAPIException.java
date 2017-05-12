package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class CarClassNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    CarClassNotFoundAPIException(Integer eventId) {
        this.code = status.toString() + "-classes-" + eventId.toString();
        this.messageApi = String.format("Car class with ID of %d not found", eventId);
    }
}
