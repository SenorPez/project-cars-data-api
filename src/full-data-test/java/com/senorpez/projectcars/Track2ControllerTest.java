package com.senorpez.projectcars;

import org.springframework.http.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;

class Track2ControllerTest {
    static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars2.v0+json", UTF_8);
    static final ClassLoader CLASS_LOADER = Track2ControllerTest.class.getClassLoader();
    static final String COLLECTION_SCHEMA = "tracks2.schema.json";
    static final String OBJECT_SCHEMA = "track2.schema.json";
    static final String ERROR_SCHEMA = "error.schema.json";
}
