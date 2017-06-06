package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;

class PageNotFoundAPIException extends APIException{
    @JsonProperty("status")
    private final Integer status = 404;
    @JsonProperty("code")
    private final String code;
    @JsonProperty("message")
    private final String messageApi;

    PageNotFoundAPIException() {
        this.code = "PAGE_NOT_FOUND";
        this.messageApi = "Page Not Found.";
    }
}
