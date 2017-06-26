package com.senorpez.projectcars;

class EventNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String messageApi;

    EventNotFoundAPIException(Integer eventId) {
        this.code = status.toString() + "-events-" + eventId.toString();
        this.messageApi = String.format("Event with ID of %d not found", eventId);
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
