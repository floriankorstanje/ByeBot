package com.florian.ScoreSystem;

public class RoleRewardEntry {
    private final String role;
    private final int score;
    private final String id;

    public RoleRewardEntry(String role, int score, String id) {
        this.role = role;
        this.score = score;
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public int getScore() {
        return score;
    }

    public String getId() {
        return id;
    }
}
