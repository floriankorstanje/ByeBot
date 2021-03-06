package com.florian.UserHistory;

public class UserHistoryEntry {
    private final String executor;
    private final long time;
    private final String offense;
    private final String reason;
    private final String id;

    public UserHistoryEntry(String executor, long time, String offense, String reason, String id) {
        this.executor = executor;
        this.time = time;
        this.offense = offense;
        this.reason = reason;
        this.id = id;
    }

    public String getExecutor() {
        return executor;
    }

    public long getTime() {
        return time;
    }

    public String getOffense() {
        return offense;
    }

    public String getReason() {
        return reason;
    }

    public String getId() {
        return id;
    }
}
