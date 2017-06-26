package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class CarFilter {
    private final String field;
    private final Operation operation;
    private final String value;
    private final Set<String> orValues;

    enum Operation {
        EQUAL("equals"),
        NOTEQUAL("notequals"),
        GREATERTHAN("greaterthan"),
        LESSTHAN("lessthan");

        private final String jsonString;

        Operation(String jsonString) {
            this.jsonString = jsonString;
        }

        public static Operation fromJsonString(String jsonString) {
            for (Operation operation : values()) {
                if (operation.jsonString.equalsIgnoreCase(jsonString)) return operation;
            }
            return null;
        }

        public Predicate<Car> predicate(CarFilter carFilter) {
            return car -> {
                if (carFilter.operation == null) return true;
                try {
                    Field classField = Car.class.getDeclaredField(carFilter.field);
                    classField.setAccessible(true);
                    switch (carFilter.operation) {
                        case EQUAL:
                            if (carFilter.value == null) {
                                return carFilter.orValues.contains(classField.get(car).toString().toLowerCase());
                            } else {
                                return classField.get(car).toString().equalsIgnoreCase(carFilter.value);
                            }

                        case NOTEQUAL:
                            if (carFilter.value == null) {
                                return !carFilter.orValues.contains(classField.get(car).toString().toLowerCase());
                            } else {
                                return !classField.get(car).toString().equalsIgnoreCase(carFilter.value);
                            }

                        case GREATERTHAN:
                            return carFilter.value != null && Integer.valueOf(classField.get(car).toString()) > Integer.valueOf(carFilter.value);

                        case LESSTHAN:
                            return carFilter.value != null && Integer.valueOf(classField.get(car).toString()) < Integer.valueOf(carFilter.value);

                        default:
                            return false;

                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    return false;
                }
            };
        }
    }

    @JsonCreator
    public CarFilter(
            @JsonProperty("field") String field,
            @JsonProperty("operation") String operation,
            @JsonProperty("value") String value,
            @JsonProperty("orValues")List<String> orValues) {
        this.field = field;
        this.operation = Operation.fromJsonString(operation);
        this.value = value;
        this.orValues = orValues == null ? null : orValues.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(HashSet::new));
    }

    Predicate<Car> getOperation() {
        return operation.predicate(this);
    }
}
