package com.senorpez.projectcars;

class RoundNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String messageApi;

    RoundNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-rounds-" + carId.toString();
        this.messageApi = String.format("Round with ID of %d not found", carId);
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
