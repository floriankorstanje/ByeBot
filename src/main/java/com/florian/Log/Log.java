package com.florian.Log;

import com.florian.ErrorCode;
import com.florian.Vars;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Log {
    public static void log(String message) {
        String output = "[" + getTimestamp() + "] " + message;
        System.out.println(output);
        write(output);
    }

    public static ErrorCode clearLogs() {
        // Get all log files
        File[] files = new File(Vars.logsFolder).listFiles();

        // Save success status
        boolean success = true;

        // Loop through all files and delete them
        for (File file : files) {
            // Delete the file
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                // File couldn't be deleted
                success = false;
            }
        }

        return success ? ErrorCode.SUCCESS : ErrorCode.OTHER_ERROR;
    }

    private static void write(String message) {
        Path path = Paths.get(Vars.logFile);

        // If the file doesn't exist, create it
        if (!new File(Vars.logFile).exists()) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                // Failed to create log file
                System.out.println("Unable to create log file.");
            }
        }

        // Append message to the file
        try {
            Files.write(path, (message + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // Failed to write
            System.out.println("Failed to write to log file.");
        }
    }

    private static String getTimestamp() {
        long time = Instant.now().toEpochMilli();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        return formatter.format(new Date(time));
    }
}
