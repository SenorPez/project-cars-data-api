package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE,
        fieldVisibility = JsonAutoDetect.Visibility.NONE)
abstract class APIException extends RuntimeException {
}
