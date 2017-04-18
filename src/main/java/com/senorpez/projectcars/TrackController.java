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
@Api(tags = {"tracks"})
@RequestMapping(method = {RequestMethod.GET})
class TrackController {
    @RequestMapping(value = "/v1/tracks")
    @ApiOperation(
            value = "Lists all tracks",
            notes = "Returns a list of all tracks available through the Project CARS Data API",
            response = Track.class,
            responseContainer = "List"
    )
    public List<Track> tracks() {
        try (Connection conn = Application.DatabaseConnection()) {
            return trackQuery(conn);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/v1/tracks/{trackId}")
    @ApiOperation(
            value = "Lists all tracks",
            notes = "Returns a track as specified by its ID number",
            response = Track.class
    )
    public Track tracks(
            @ApiParam(
                    value = "ID of track to return",
                    required = true
            )
            @PathVariable Integer trackId) {
        try (Connection conn = Application.DatabaseConnection()) {
            return trackQuery(conn, trackId);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static List<Track> processTrackResults(final ResultSet trackResults) throws SQLException {
        final List<Track> tracks = new ArrayList<>();
        while (trackResults.next()) tracks.add(new Track(trackResults));
        return tracks;
    }

    private static List<Track> trackQuery(final Connection conn) throws SQLException {
        try (
                final Statement trackStmt = conn.createStatement();
                final ResultSet trackResults = trackStmt.executeQuery(
                        "SELECT " + Track.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                                " FROM " + Track.DB_TABLE_NAME + ";"
                )
        ) {
            return processTrackResults(trackResults);
        }
    }

    static Track trackQuery(final Connection conn, final Integer trackId) throws SQLException {
        try (final PreparedStatement trackStmt = conn.prepareStatement(
                "SELECT " + Track.DB_COLUMNS.stream().collect(Collectors.joining(", ")) +
                        " FROM " + Track.DB_TABLE_NAME +
                        " WHERE id = ?;")
        ) {
            trackStmt.setInt(1, trackId);
            try (final ResultSet trackResults = trackStmt.executeQuery()) {
                List<Track> tracks = processTrackResults(trackResults);
                return (tracks.size() == 1) ? tracks.get(0) : null;
            }
        }
    }
}
