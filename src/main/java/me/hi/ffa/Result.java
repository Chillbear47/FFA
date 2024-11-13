package me.hi.ffa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Result {
    private int kills;
    private List<String> kitsunlocked;
    private int killstreak;
    private int points;
    private int deaths;
    private double multiplier;

    // No-argument constructor with default values
    public Result() {
        this.kills = 0;
        this.kitsunlocked = new ArrayList<>(Arrays.asList("default"));
        this.killstreak = 0;
        this.points = 0;
        this.deaths = 0;
        this.multiplier = 1.0;
    }

    // Constructor with all fields
    public Result(int kills, List<String> kitsunlocked, int killstreak, int points, int deaths, double multiplier) {
        this.kills = kills;
        this.kitsunlocked = kitsunlocked;
        this.killstreak = killstreak;
        this.points = points;
        this.deaths = deaths;
        this.multiplier = multiplier;
    }

    public int getKills() { return kills; }
    public int getKillstreak() { return killstreak; }
    public int getPoints() { return points; }
    public List<String> getKitsUnlocked() {return kitsunlocked;}
    public int getDeaths() { return deaths; }
    public double getMultiplier() { return multiplier; }

    @Override
    public String toString() {
        return "Result{" +
                "kills=" + kills +
                ", kitsunlocked=" + kitsunlocked +
                ", killstreak=" + killstreak +
                ", points=" + points +
                ", deaths=" + deaths +
                ", multiplier=" + multiplier +
                '}';
    }
}