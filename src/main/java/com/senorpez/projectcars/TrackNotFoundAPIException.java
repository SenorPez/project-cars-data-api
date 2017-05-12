package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class TrackNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    TrackNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-tracks-" + carId.toString();
        this.messageApi = String.format("Track with ID of %d not found", carId);
    }

    TrackNotFoundAPIException(String location, String variation) {
        this.code = status.toString() + "-tracks-" + location + "-" + variation;
        this.messageApi = String.format("Track with location of %s and variation of %s not found", location, variation);
    }
}
