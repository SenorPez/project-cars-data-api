package com.senorpez.projectcars;

class Round {
    private final Integer id;
    private final Track track;
    private final Integer laps;
    private final Integer time;

    Round(Integer id, Track track, Integer laps, Integer time) {
        this.id = id;
        this.track = track;
        this.laps = laps;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public Track getTrack() {
        return track;
    }

    public Integer getLaps() {
        return laps;
    }

    public Integer getTime() {
        return time;
    }
}
