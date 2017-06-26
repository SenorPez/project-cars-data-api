package com.senorpez.projectcars;

class TrackNotFoundAPIException extends APIException {
    private final Integer status = 404;
    private final String code;
    private final String messageApi;

    TrackNotFoundAPIException(Integer carId) {
        this.code = status.toString() + "-tracks-" + carId.toString();
        this.messageApi = String.format("Track with ID of %d not found", carId);
    }

    TrackNotFoundAPIException(String location, String variation) {
        this.code = status.toString() + "-tracks-" + location + "-" + variation;
        this.messageApi = String.format("Track with location of %s and variation of %s not found", location, variation);
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
