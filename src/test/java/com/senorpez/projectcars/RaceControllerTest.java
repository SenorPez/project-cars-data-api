package com.senorpez.projectcars;

import org.springframework.http.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;

class RaceControllerTest {
    static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars.v1+json", UTF_8);
    static final ClassLoader CLASS_LOADER = RoundControllerTest.class.getClassLoader();
    static final String COLLECTION_SCHEMA = "races.schema.json";
    static final String OBJECT_SCHEMA = "race.schema.json";
    static final String ERROR_SCHEMA = "error.schema.json";
}
