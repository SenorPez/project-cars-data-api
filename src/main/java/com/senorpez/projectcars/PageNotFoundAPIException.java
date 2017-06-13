package com.senorpez.projectcars;

class PageNotFoundAPIException extends APIException{
    private final String code;
    private final String messageApi;

    PageNotFoundAPIException() {
        this.code = "PAGE_NOT_FOUND";
        this.messageApi = "Page Not Found.";
    }

    @Override
    public Integer getStatus() {
        return 404;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return messageApi;
    }
}
