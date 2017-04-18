package com.senorpez.projectcars;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = {"cars"})
@RequestMapping(method = {RequestMethod.GET})
class CarController {
    @RequestMapping(value = "/v1/cars")
    @ApiOperation(
            value = "Lists all cars available",
            notes = "Returns a list of all cars available through the Project CARS Data API",
            response = Car.class,
            responseContainer = "List"
    )
    public List<Car> cars() {
        try (Connection conn = Application.DatabaseConnection()) {
            return carQuery(conn);
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
            return carQuery(conn, carId);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Car> processCarResults(final ResultSet carResults) throws SQLException {
        final List<Car> cars = new ArrayList<>();
        while (carResults.next()) cars.add(new Car(carResults));
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

    static List<Car> carQuery(final Connection conn, final String queryString) throws SQLException {
        try (
                final Statement carStmt = conn.createStatement();
                final ResultSet carResults = carStmt.executeQuery(
                        "SELECT " + Car.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                                " FROM " + Car.DB_TABLE_NAME + " WHERE " + queryString + ";"
                )
        ) {
            return processCarResults(carResults);
        }
    }

    static Car carQuery(final Connection conn, final String queryString, final Integer carId) throws SQLException {
        try (
                final PreparedStatement carStmt = conn.prepareStatement(
                        "SELECT " + Car.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                                " FROM " + Car.DB_TABLE_NAME + " WHERE " + queryString + " AND " +
                                " id = ?;"
                )
        ) {
            carStmt.setInt(1, carId);
            try (final ResultSet carResults = carStmt.executeQuery()) {
                List<Car> cars = processCarResults(carResults);
                return (cars.size() == 1) ? cars.get(0) : null;
            }
        }
    }
}
