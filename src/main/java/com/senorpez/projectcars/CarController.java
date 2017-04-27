package com.senorpez.projectcars;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Api(tags = {"cars"})
@RequestMapping(method = {RequestMethod.GET})
class CarController {
    class CarList extends ResourceSupport {
        @JsonProperty("cars")
        private final List<Car> carList;

        CarList(List<Car> carList) {
            this.carList = carList;
        }
    }

    @RequestMapping(value = "/v1/cars")
    @ApiOperation(
            value = "Lists all cars available",
            notes = "Returns a list of all cars available through the Project CARS Data API",
            response = Car.class,
            responseContainer = "List"
    )
    public CarList cars() {
        try (Connection conn = Application.DatabaseConnection()) {
            CarList cars = new CarList(carQuery(conn));
            cars.add(linkTo(methodOn(CarController.class).cars()).withSelfRel());
            return cars;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/cars/{carId}")
    @ApiOperation(
            value = "Returns a car",
            notes = "Returns a car as specified by its ID number",
            response = Car.class
    )
    public Car cars(
            @ApiParam(
                    value = "ID of car to return",
                    required = true
            )
            @PathVariable Integer carId) {
        try (Connection conn = Application.DatabaseConnection()) {
            Car car = carQuery(conn, carId);
            if (car != null) car.add(linkTo(methodOn(CarController.class).cars(carId)).withSelfRel());
            return car;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/events/{eventId}/cars")
    @ApiOperation(
            value = "Lists all cars eligible for an event",
            notes = "Returns a list of all cars eligible for a single player event",
            response = Car.class,
            responseContainer = "List"
    )
    public CarList eventCars(@PathVariable Integer eventId) {
        try (Connection conn = Application.DatabaseConnection()) {
            Event event = EventController.eventQuery(conn, eventId);
            if (event != null) {
                CarList cars = new CarList(carQuery(conn, eventId, event.getCarFilter()));
                cars.add(linkTo(methodOn(CarController.class).eventCars(eventId)).withSelfRel());
                cars.add(linkTo(methodOn(EventController.class).events(eventId)).withRel("parent"));
                return cars;
            } else return null;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/events/{eventId}/cars/{carId}")
    @ApiOperation(
            value = "Returns an eligible car for an event",
            notes = "Returns an eligible car for an event",
            response = Car.class
    )
    public Car eventCars(
            @ApiParam(
                    value = "ID of event",
                    required = true
            )
            @PathVariable Integer eventId,
            @ApiParam(
                    value = "ID of car",
                    required = true
            )
            @PathVariable Integer carId) {
        try (Connection conn = Application.DatabaseConnection()) {
            return CarController.carQuery(conn, eventId, carId);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Car> processCarResults(final ResultSet carResults) throws SQLException {
        final List<Car> cars = new ArrayList<>();
        while (carResults.next()) {
            Car car = new Car(carResults);
            car.add(linkTo(methodOn(CarController.class).cars(car.getCarId())).withSelfRel());
            cars.add(car);
        }
        return cars;
    }

    private static List<Car> processCarResults(final Integer eventId, final ResultSet carResults) throws SQLException {
        final List<Car> cars = new ArrayList<>();
        while (carResults.next()) {
            Car car = new Car(carResults);
            car.add(linkTo(methodOn(CarController.class).eventCars(eventId, car.getCarId())).withSelfRel());
            car.add(linkTo(methodOn(CarController.class).cars(car.getCarId())).withRel("car"));
            car.add(linkTo(methodOn(CarController.class).eventCars(eventId)).withRel("parent"));
            cars.add(car);
        }
        return cars;
    }

    private static List<Car> carQuery(final Connection conn) throws SQLException {
        try (
                final Statement carStmt = conn.createStatement();
                final ResultSet carResults = carStmt.executeQuery(
                        "SELECT " + Car.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                                " FROM " + Car.DB_TABLE_NAME  + ";"
                )
        ) {
            return processCarResults(carResults);
        }
    }

    private static Car carQuery(final Connection conn, final Integer carId) throws SQLException {
        try (final PreparedStatement carStmt = conn.prepareStatement(
                "SELECT " + Car.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Car.DB_TABLE_NAME +
                        " WHERE id = ?;")
        ) {
            carStmt.setInt(1, carId);
            try (final ResultSet carResults = carStmt.executeQuery()) {
                List<Car> cars = processCarResults(carResults);
                return (cars.size() == 1) ? cars.get(0) : null;
            }
        }
    }

    static List<Car> carQuery(final Connection conn, final Integer eventId, final String queryString) throws SQLException {
        try (
                final Statement carStmt = conn.createStatement();
                final ResultSet carResults = carStmt.executeQuery(
                        "SELECT " + Car.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                                " FROM " + Car.DB_TABLE_NAME + " WHERE " + queryString + ";"
                )
        ) {
            return processCarResults(eventId, carResults);
        }
    }

    private static Car carQuery(final Connection conn, final Integer eventId, final Integer carId) throws SQLException {
        try (final PreparedStatement carStmt = conn.prepareStatement(
                "SELECT " + Car.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Car.DB_TABLE_NAME +
                        " WHERE id = ?;")
        ) {
            carStmt.setInt(1, carId);
            try (final ResultSet carResults = carStmt.executeQuery()) {
                List<Car> cars = processCarResults(eventId, carResults);
                return (cars.size() == 1) ? cars.get(0) : null;
            }
        }
    }

}
