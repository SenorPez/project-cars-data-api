package com.senorpez.projectcars;

class Car {
    private final Integer id;
    private final String manufacturer;
    private final String model;
    private final String country;
    private final String carClass;

    Car(Integer id, String manufacturer, String model, String country, String carClass) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
        this.country = country;
        this.carClass = carClass;
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

    public String getCountry() {
        return country;
    }

    public String getCarClass() {
        return carClass;
    }
}
