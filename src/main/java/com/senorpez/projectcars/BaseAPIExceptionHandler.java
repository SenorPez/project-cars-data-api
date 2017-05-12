package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

abstract class BaseAPIExceptionHandler {
    private static final ExceptionMapping DEFAULT_ERROR = new ExceptionMapping(
            "SERVER_ERROR",
            "Internal server error.",
            INTERNAL_SERVER_ERROR);
    private final Map<Class, ExceptionMapping> exceptionMappings = new HashMap<>();

    BaseAPIExceptionHandler() {
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    ErrorResponse handleThrowable(final Throwable exception, final HttpServletResponse response) {
        ExceptionMapping mapping = exceptionMappings.getOrDefault(exception.getClass(), DEFAULT_ERROR);
        response.setStatus(mapping.status.value());
        return new ErrorResponse(mapping.code, mapping.message);
    }

    @ExceptionHandler(APIException.class)
    @ResponseBody
    APIException handleAPIException(final APIException exception, final HttpServletResponse response) {
        ExceptionMapping mapping = exceptionMappings.getOrDefault(exception.getClass(), DEFAULT_ERROR);
        response.setStatus(mapping.status.value());
        return exception;
    }

    void registerMapping(
            final Class<?> clazz,
            final String code,
            final String message,
            final HttpStatus status) {
        exceptionMappings.put(clazz, new ExceptionMapping(code, message, status));
    }

    class ErrorResponse {
        @JsonProperty("code")
        private final String code;
        @JsonProperty("message")
        private final String message;

        @JsonCreator
        ErrorResponse(
                @JsonProperty("code") String code,
                @JsonProperty("message") String message) {
            this.code = code;
            this.message = message;
        }
    }

    private static class ExceptionMapping {
        private final String message;
        private final String code;
        private final HttpStatus status;

        ExceptionMapping(String message, String code, HttpStatus status) {
            this.message = message;
            this.code = code;
            this.status = status;
        }
    }
}
