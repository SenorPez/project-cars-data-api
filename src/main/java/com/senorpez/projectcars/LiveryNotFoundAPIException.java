package com.senorpez.projectcars;

class LiveryNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String messageApi;

    LiveryNotFoundAPIException(Integer liveryId) {
        this.code = status.toString() + "-liveries-" + liveryId.toString();
        this.messageApi = String.format("Livery with ID of %d not found", liveryId);
    }

    @Override
    public Integer getStatus() {
        return status;
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
