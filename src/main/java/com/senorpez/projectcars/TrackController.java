package com.senorpez.projectcars;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
class TrackController {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:~/projectcars";
    private static final String USER_NAME = "project_cars_api_user";
    private static final String USER_PASS = "o5RXD}XL!-K2";

    @RequestMapping(value = "/v1/tracks")
    public List<Track> tracks() {
        Connection conn = null;
        Statement stmt = null;
        String sql = "SELECT id, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ " +
                "FROM tracks;";
        List<Track> tracks = new ArrayList<>();

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet trackResults = stmt.executeQuery(sql);
            while (trackResults.next()) {
                Integer trackId = trackResults.getInt("id");
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

                tracks.add(new Track(
                        trackId,
                        location,
                        variation,
                        length,
                        pitEntryX,
                        pitEntryZ,
                        pitExitX,
                        pitExitZ
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
        String sql = "SELECT id, location, variation, length, pitEntryX, pitEntryZ, pitExitX, pitExitZ " +
                "FROM tracks " +
                "WHERE id = " + id + ";";
        Track track = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER_NAME, USER_PASS);
            stmt = conn.createStatement();

            ResultSet trackResults = stmt.executeQuery(sql);
            while (trackResults.next()) {
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

                track = new Track(
                        id,
                        location,
                        variation,
                        length,
                        pitEntryX,
                        pitEntryZ,
                        pitExitX,
                        pitExitZ);
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
