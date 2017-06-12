package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class LiveryNotFoundAPIException extends APIException {
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    LiveryNotFoundAPIException(Integer liveryId) {
        this.code = status.toString() + "-liveries-" + liveryId.toString();
        this.messageApi = String.format("Livery with ID of %d not found", liveryId);
    }
}
