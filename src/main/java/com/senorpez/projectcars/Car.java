package com.senorpez.projectcars;

class Car {
    private final Integer id;
    private final String manufacturer;
    private final String model;

    Car(Integer id, String manufacturer, String model) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
    }

    public Integer getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }
}
