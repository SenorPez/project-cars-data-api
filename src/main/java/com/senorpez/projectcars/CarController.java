package com.senorpez.projectcars;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
class CarController {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://pcarsapi.cbwuidepjacv.us-west-2.rds.amazonaws.com:3306/";

    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:~/projectcars;MODE=mysql";

    private static final String USER_NAME = "pcarsapi_user";
    private static final String USER_PASS = "F=R4tV}p:Jb2>VqJ";

    @RequestMapping(value = "/v1/cars")
    public List<Car> cars() {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT id, manufacturer, model, class, country FROM cars;";
        List<Car> cars = new ArrayList<>();

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

            ResultSet carResults = stmt.executeQuery(sql);
            while (carResults.next()) {
                Integer carId = carResults.getInt("id");
                String manufacturer = carResults.getString("manufacturer");
                String model = carResults.getString("model");
                String country = carResults.getString("country");
                String carClass = carResults.getString("class");

                cars.add(new Car(
                        carId,
                        manufacturer,
                        model,
                        country,
                        carClass
                ));
            }

            carResults.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return cars;
    }

    @RequestMapping(value = "/v1/cars/{carId}")
    public Car cars(@PathVariable Integer carId) {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT manufacturer, model, class, country FROM cars WHERE id = " + carId + ";";
        Car car = null;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
                stmt.execute("USE projectcarsapi;");
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
                stmt = conn.createStatement();
            }

            ResultSet carResults = stmt.executeQuery(sql);
            while (carResults.next()) {
                String manufacturer = carResults.getString("manufacturer");
                String model = carResults.getString("model");
                String country = carResults.getString("country");
                String carClass = carResults.getString("class");

                car = new Car(carId, manufacturer, model, country, carClass);
            }

            carResults.close();
            stmt.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return car;
    }
}
