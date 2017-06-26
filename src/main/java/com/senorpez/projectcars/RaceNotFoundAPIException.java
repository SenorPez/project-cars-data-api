package com.senorpez.projectcars;

class RaceNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String messageApi;

    RaceNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-races-" + carId.toString();
        this.messageApi = String.format("Race with ID of %d not found", carId);
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
