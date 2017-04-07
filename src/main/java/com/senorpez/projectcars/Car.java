package com.senorpez.projectcars;

class Car {
    private final Integer id;
    private final String manufacturer;
    private final String model;
    private final String carClass;

    Car(Integer id, String manufacturer, String model, String carClass) {
        this.id = id;
        this.manufacturer = manufacturer;
        this.model = model;
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

    public String getCarClass() {
        return carClass;
    }
}
