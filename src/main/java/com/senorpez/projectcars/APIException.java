package com.senorpez.projectcars;

abstract class APIException extends RuntimeException {
    abstract public Integer getStatus();
    abstract public String getCode();
    abstract public String getMessage();
}
