package com.florian.Userlog;

public class UserlogEntry {
    private final long time;
    private final String action;

    public UserlogEntry(long time, String action) {
        this.time = time;
        this.action = action;
    }

    public long getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }
}
