package com.senorpez.projectcars;

class CarClassNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String messageApi;

    CarClassNotFoundAPIException(Integer eventId) {
        this.code = status.toString() + "-classes-" + eventId.toString();
        this.messageApi = String.format("Car class with ID of %d not found", eventId);
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
