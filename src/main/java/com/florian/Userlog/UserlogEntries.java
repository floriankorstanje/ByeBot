package com.florian.Userlog;

import com.florian.ErrorCode;

public class UserlogEntries {
    private UserlogEntry[] entries;
    private ErrorCode error;

    public UserlogEntries(UserlogEntry[] entries, ErrorCode error) {
        this.entries = entries;
        this.error = error;
    }

    public UserlogEntry[] getEntries() {
        return entries;
    }

    public ErrorCode getErrorCode() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public void setEntries(UserlogEntry[] entries) {
        this.entries = entries;
    }
}
