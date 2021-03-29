package com.florian.Reminders;

public class ReminderEntry {
    private final String id;
    private final String user;
    private final long time;
    private final String reason;

    public ReminderEntry(String id, String user, long time, String reason) {
        this.id = id;
        this.user = user;
        this.time = time;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }
}
