package com.senorpez.projectcars;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
class TrackController {
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_URL = "jdbc:mysql://pcarsapi.cbwuidepjacv.us-west-2.rds.amazonaws.com:3306/";

    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_URL = "jdbc:h2:~/projectcars;MODE=mysql";

    private static final String USER_NAME = "pcarsapi_user";
    private static final String USER_PASS = "F=R4tV}p:Jb2>VqJ";

    @RequestMapping(value = "/v1/tracks")
    public List<Track> tracks() {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT id, name, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ, gridSize " +
                "FROM tracks;";
        List<Track> tracks = new ArrayList<>();

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
            }
            stmt = conn.createStatement();

            ResultSet trackResults = stmt.executeQuery(sql);
            while (trackResults.next()) {
                Integer trackId = trackResults.getInt("id");
                String name = trackResults.getString("name");
                String location = trackResults.getString("location");
                String variation = trackResults.getString("variation");
                Float length = trackResults.getFloat("length");

                Float pitEntryX = trackResults.getFloat("pitEntryX");
                if (trackResults.wasNull()) pitEntryX = null;
                Float pitEntryZ = trackResults.getFloat("pitEntryZ");
                if (trackResults.wasNull()) pitEntryZ = null;

                Float pitExitX = trackResults.getFloat("pitExitX");
                if (trackResults.wasNull()) pitExitX = null;
                Float pitExitZ = trackResults.getFloat("pitExitZ");
                if (trackResults.wasNull()) pitExitZ = null;

                Integer gridSize = trackResults.getInt("gridSize");

                tracks.add(new Track(
                        trackId,
                        name,
                        location,
                        variation,
                        length,
                        pitEntryX,
                        pitEntryZ,
                        pitExitX,
                        pitExitZ,
                        gridSize
                ));
            }

            trackResults.close();
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
        return tracks;
    }

    @RequestMapping(value = "/v1/tracks/{id}")
    public Track tracks(@PathVariable Integer id) {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT name, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ, gridSize " +
                "FROM tracks " +
                "WHERE id = " + id + ";";
        Track track = null;

        try {
            try {
                Class.forName(MYSQL_DRIVER);
                conn = DriverManager.getConnection(MYSQL_URL, USER_NAME, USER_PASS);
            } catch (ClassNotFoundException | SQLException e) {
                Class.forName(H2_DRIVER);
                conn = DriverManager.getConnection(H2_URL, USER_NAME, USER_PASS);
            }
            stmt = conn.createStatement();

            ResultSet trackResults = stmt.executeQuery(sql);
            while (trackResults.next()) {
                String name = trackResults.getString("name");
                String location = trackResults.getString("location");
                String variation = trackResults.getString("variation");
                Float length = trackResults.getFloat("length");

                Float pitEntryX = trackResults.getFloat("pitEntryX");
                if (trackResults.wasNull()) pitEntryX = null;
                Float pitEntryZ = trackResults.getFloat("pitEntryZ");
                if (trackResults.wasNull()) pitEntryZ = null;

                Float pitExitX = trackResults.getFloat("pitExitX");
                if (trackResults.wasNull()) pitExitX = null;
                Float pitExitZ = trackResults.getFloat("pitExitZ");
                if (trackResults.wasNull()) pitExitZ = null;

                Integer gridSize = trackResults.getInt("gridSize");

                track = new Track(
                        id,
                        name,
                        location,
                        variation,
                        length,
                        pitEntryX,
                        pitEntryZ,
                        pitExitX,
                        pitExitZ,
                        gridSize);
            }

            trackResults.close();
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
        return track;
    }
}
