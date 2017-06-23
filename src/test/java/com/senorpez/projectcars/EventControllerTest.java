package com.senorpez.projectcars;

import org.springframework.http.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;

class EventControllerTest {
    static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars.v1+json", UTF_8);
    static final ClassLoader CLASS_LOADER = EventControllerTest.class.getClassLoader();
    static final String COLLECTION_SCHEMA = "events.schema.json";
    static final String CAR_COLLECTION_SCHEMA = "cars.schema.json";
    static final String OBJECT_SCHEMA = "event.schema.json";
    static final String CAR_OBJECT_SCHEMA = "car.schema.json";
    static final String ERROR_SCHEMA = "error.schema.json";
    static final String CAR_CLASS_OBJECT_SCHEMA = "class.schema.json";
    static final String LIVERY_COLLECTION_SCHEMA = "liveries.schema.json";
    static final String LIVERY_OBJECT_SCHEMA = "livery.schema.json";
}