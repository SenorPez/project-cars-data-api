package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class RoundNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    RoundNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-rounds-" + carId.toString();
        this.messageApi = String.format("Round with ID of %d not found", carId);
    }
}
