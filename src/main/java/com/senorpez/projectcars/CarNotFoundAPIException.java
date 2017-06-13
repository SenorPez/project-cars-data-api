package com.senorpez.projectcars;

class CarNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String message;

    CarNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-cars-" + carId.toString();
        this.message = String.format("Car with ID of %d not found", carId);
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
        return message;
    }
}
