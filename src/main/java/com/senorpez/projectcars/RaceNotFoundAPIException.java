package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class RaceNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    RaceNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-races-" + carId.toString();
        this.messageApi = String.format("Race with ID of %d not found", carId);
    }
}
