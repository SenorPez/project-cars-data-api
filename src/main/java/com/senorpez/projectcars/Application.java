package com.senorpez.projectcars;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SpringBootApplication
public class Application {
    static final Set<Car> CARS = Collections.unmodifiableSet(getData(Car.class, "cars"));
    static final Set<CarClass> CAR_CLASSES = Collections.unmodifiableSet(getData(CarClass.class, "classes"));
    static final Set<Track> TRACKS = Collections.unmodifiableSet(getData(Track.class, "tracks"));
    static final Set<Event> EVENTS = Collections.unmodifiableSet(getData(Event.class, "events"));

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    private static <T> Set<T> getData(Class objectClass, String field) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ClassLoader classLoader = Application.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("data.json");
            ObjectNode jsonData = mapper.readValue(inputStream, ObjectNode.class);
            return mapper.readValue(jsonData.get(field).toString(), mapper.getTypeFactory().constructCollectionType(Set.class, objectClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }

    static <T> Set<T> getData(Class objectClass, JsonNode jsonData) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonData.toString(), mapper.getTypeFactory().constructCollectionType(Set.class, objectClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new HashSet<>();
    }
}
