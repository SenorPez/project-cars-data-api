package com.senorpez.projectcars;

class CarBuilder {
    private Integer id = 11;
    private String manufacturer = "Default Manufacturer";
    private String model = "Default Model";
    private String country = "Default Country";
    private String carClass = "Default Car Class";
    private Integer year = 1979;
    private Car.Drivetrain drivetrain = Car.Drivetrain.FWD;
    private Car.EnginePosition enginePosition = Car.EnginePosition.FRONT;
    private String engineType = "Default Engine Type";
    private Integer topSpeed = 100;
    private Integer horsepower = 420;
    private Float acceleration = 1.42f;
    private Float braking = 1.42f;
    private Integer weight = 1000;
    private Integer torque = 1000;
    private Integer weightBalance = 50;
    private Float wheelbase = 1.42f;
    private Car.ShiftPattern shiftPattern = Car.ShiftPattern.SEQUENTIAL;
    private Car.Shifter shifter = Car.Shifter.PADDLES;
    private Integer gears = 6;
    private String dlc = null;

    CarBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    CarBuilder withManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    CarBuilder withModel(String model) {
        this.model = model;
        return this;
    }

    CarBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    CarBuilder withCarClass(String carClass) {
        this.carClass = carClass;
        return this;
    }

    CarBuilder withYear(Integer year) {
        this.year = year;
        return this;
    }

    CarBuilder withDrivetrain(Car.Drivetrain drivetrain) {
        this.drivetrain = drivetrain;
        return this;
    }

    CarBuilder withEnginePosition(Car.EnginePosition enginePosition) {
        this.enginePosition = enginePosition;
        return this;
    }

    CarBuilder withEngineType(String engineType) {
        this.engineType = engineType;
        return this;
    }

    CarBuilder withTopSpeed(Integer topSpeed) {
        this.topSpeed = topSpeed;
        return this;
    }

    CarBuilder withHorsepower(Integer horsepower) {
        this.horsepower = horsepower;
        return this;
    }

    CarBuilder withAcceleration(Float acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    CarBuilder withBraking(Float braking) {
        this.braking = braking;
        return this;
    }

    CarBuilder withWeight(Integer weight) {
        this.weight = weight;
        return this;
    }

    CarBuilder withTorque(Integer torque) {
        this.torque = torque;
        return this;
    }

    CarBuilder withWeightBalance(Integer weightBalance) {
        this.weightBalance = weightBalance;
        return this;
    }

    CarBuilder withWheelbase(Float wheelbase) {
        this.wheelbase = wheelbase;
        return this;
    }

    CarBuilder withShiftPattern(Car.ShiftPattern shiftPattern) {
        this.shiftPattern = shiftPattern;
        return this;
    }

    CarBuilder withShifter(Car.Shifter shifter) {
        this.shifter = shifter;
        return this;
    }

    CarBuilder withGears(Integer gears) {
        this.gears = gears;
        return this;
    }

    CarBuilder withDlc(String dlc) {
        this.dlc = dlc;
        return this;
    }

    Car build() {
        return new Car(
                id, manufacturer, model, country, carClass, year, drivetrain,
                enginePosition, engineType, topSpeed, horsepower, acceleration,
                braking, weight, torque, weightBalance, wheelbase, shiftPattern,
                shifter, gears, dlc);
    }
}
