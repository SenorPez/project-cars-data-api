package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@RestControllerAdvice
class APIExceptionHandler {
    APIExceptionHandler() {
    }

    @ExceptionHandler(APIException.class)
    @ResponseBody
    ErrorResponse handleApiException(final APIException exception, final HttpServletResponse response) {
        response.setStatus(exception.getStatus());
        return new ErrorResponse(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    NotAcceptableResponse handleNotAcceptable(final HttpMediaTypeNotAcceptableException exception, final HttpServletResponse response) {
        response.setStatus(NOT_ACCEPTABLE.value());
        Set<String> formats = new HashSet<>(Arrays.asList(
                "application/vnd.senorpez.pcars.v1+json; charset=utf-8",
                "application/vnd.senorpez.pcars2.v0+json; charset=utf-8"));
        return new NotAcceptableResponse(formats);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    ErrorResponse handleThrowable(final Throwable exception, final HttpServletResponse response) {
        response.setStatus(INTERNAL_SERVER_ERROR.value());
        return new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected or server error has occurred.");
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

    class NotAcceptableResponse extends ErrorResponse {
        @JsonProperty("formats")
        private final Set<String> formats;

        @JsonCreator
        NotAcceptableResponse(
                @JsonProperty("formats") Set<String> formats) {
            super("406", "Accept header incorrect");
            this.formats = formats;
        }
    }
}
