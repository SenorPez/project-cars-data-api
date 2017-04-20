package com.senorpez.projectcars;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(method = {RequestMethod.GET})
public class RootController {
    @RequestMapping(value = "/v1")
    public Map<String, String> root() {
        Map<String, String> endPoints = new HashMap<>();
        endPoints.put("cars", "http://projectcars.senorpez.com/v1/cars{/carID}");
        endPoints.put("events", "http://projectcars.senorpez.com/v1/events{/eventID}");
        endPoints.put("event_cars", "http://projectcars.senorpez.com/v1/events/{eventID}/cars{/carID}");
        endPoints.put("event_rounds", "http://projectcars.senorpez.com/v1/events/{eventID}/rounds{/roundID}");
        endPoints.put("round_races", "http://projectcars.senorpez.com/v1/events/{eventID}/rounds/{roundID}/races{/raceID}");
        endPoints.put("tracks", "http://projectcars.senorpez.com/v1/tracks{/trackID}");
        return endPoints;
    }
}
