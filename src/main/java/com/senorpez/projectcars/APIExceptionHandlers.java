package com.senorpez.projectcars;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class APIExceptionHandlers extends BaseAPIExceptionHandler {
    public APIExceptionHandlers() {
        registerMapping(CarNotFoundAPIException.class, "CAR_NOT_FOUND", "Car Not Found.", HttpStatus.NOT_FOUND);
        registerMapping(CarClassNotFoundAPIException.class, "CAR_CLASS_NOT_FOUND", "Car Class Not Found.", HttpStatus.NOT_FOUND);
        registerMapping(EventNotFoundAPIException.class, "EVENT_NOT_FOUND", "Event Not Found.", HttpStatus.NOT_FOUND);
        registerMapping(RaceNotFoundAPIException.class, "RACE_NOT_FOUND", "Race Not Found.", HttpStatus.NOT_FOUND);
        registerMapping(RoundNotFoundAPIException.class, "ROUND_NOT_FOUND", "Round Not Found.", HttpStatus.NOT_FOUND);
        registerMapping(TrackNotFoundAPIException.class, "TRACK_NOT_FOUND", "Track Not Found.", HttpStatus.NOT_FOUND);
    }
}
