package com.florian.ScoreSystem;

public class UserScore {
    private final String user;
    private final int score;

    public UserScore(String user, int score) {
        this.user = user;
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }
}
