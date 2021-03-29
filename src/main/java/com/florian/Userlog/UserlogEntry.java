package com.florian.Userlog;

public class UserlogEntry {
    private final String user;
    private final long time;
    private final String action;

    public UserlogEntry(String user, long time, String action) {
        this.user = user;
        this.time = time;
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }
}
