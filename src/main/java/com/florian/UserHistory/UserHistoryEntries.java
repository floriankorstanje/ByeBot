package com.florian.UserHistory;

import com.florian.ErrorCode;

public class UserHistoryEntries {
    private UserHistoryEntry[] entries;
    private ErrorCode error;

    public UserHistoryEntries(UserHistoryEntry[] entries, ErrorCode error) {
        this.entries = entries;
        this.error = error;
    }

    public UserHistoryEntry[] getEntries() {
        return entries;
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public void setEntries(UserHistoryEntry[] entries) {
        this.entries = entries;
    }
}
