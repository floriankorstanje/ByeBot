package com.florian.Reminders;

public class ReminderEntry {
    private final String id;
    private final String user;
    private final String channel;
    private final long time;
    private final String reason;

    public ReminderEntry(String id, String user, String channel, long time, String reason) {
        this.id = id;
        this.user = user;
        this.channel = channel;
        this.time = time;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getChannel() {
        return channel;
    }

    public long getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }
}
