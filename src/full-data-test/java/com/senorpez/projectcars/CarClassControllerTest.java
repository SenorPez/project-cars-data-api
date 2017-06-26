package com.senorpez.projectcars;

import org.springframework.http.MediaType;

import static java.nio.charset.StandardCharsets.UTF_8;

class CarClassControllerTest {
    static final MediaType MEDIA_TYPE = new MediaType("application", "vnd.senorpez.pcars.v1+json", UTF_8);
    static final ClassLoader CLASS_LOADER = CarClassControllerTest.class.getClassLoader();
    static final String COLLECTION_SCHEMA = "classes.schema.json";
    static final String OBJECT_SCHEMA = "class.schema.json";
    static final String ERROR_SCHEMA = "error.schema.json";
}