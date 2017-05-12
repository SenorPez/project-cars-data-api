package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class CarNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    CarNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-cars-" + carId.toString();
        this.messageApi = String.format("Car with ID of %d not found", carId);
    }
}
