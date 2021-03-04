package com.florian.UserHistory;

import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

public class UserHistory {
    public static ErrorCode addEntry(Guild g, Member offender, Member executor, OffenseType type, String entry) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + offender.getId();

        // Check if folder exists
        File historyFolder = new File(folder);
        if(!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if(!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Check if file exists
        if(!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readSmallTextFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read user history for user " + offender.getId() + "(" + offender.getUser().getAsTag() + ") in server " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Make sure lines isn't null
        if(lines == null)
            return ErrorCode.OTHER_ERROR;


        // Get current epoch time (ms)
        long time = Instant.now().toEpochMilli();

        // Add this entry to the list
        // Entries are formatted as following: executor-id,epoch-time,offense,entry
        lines.add(executor.getId() + "," + time + "," + type.toString() + "," + entry);

        // Write changes back to file
        try {
            Util.writeSmallTextFile(file, lines);
        } catch (Exception e) {
            System.out.println("Couldn't write user history for user " + offender.getId() + "(" + offender.getUser().getAsTag() + ") in server " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode removeEntry(Guild g, Member m, int entry) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + m.getId();

        // Check if folder exists
        File historyFolder = new File(folder);
        if(!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if(!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }

            // If it did create, return no history because there is no history files for this server
            return ErrorCode.NO_USER_HISTORY;
        }

        // If the file doesn't exist, there is also no history
        if(!new File(file).exists())
            return ErrorCode.NO_USER_HISTORY;

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readSmallTextFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read user history for user " + m.getId() + "(" + m.getUser().getAsTag() + ") in server " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Make sure lines isn't null
        if(lines == null)
            return ErrorCode.OTHER_ERROR;

        // Remove the entry we want to remove
        lines.remove(entry);

        // Write changes back to file
        try {
            Util.writeSmallTextFile(file, lines);
        } catch (Exception e) {
            System.out.println("Couldn't write user history for user " + m.getId() + "(" + m.getUser().getAsTag() + ") in server " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode editEntry(Guild g, Member m, int entry, String newReason) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + m.getId();

        // Check if folder exists
        File historyFolder = new File(folder);
        if(!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if(!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }

            // If it did create, return no history because there is no history files for this server
            return ErrorCode.NO_USER_HISTORY;
        }

        // If the file doesn't exist, there is also no history
        if(!new File(file).exists())
            return ErrorCode.NO_USER_HISTORY;

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readSmallTextFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read user history for user " + m.getId() + "(" + m.getUser().getAsTag() + ") in server " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Make sure lines isn't null
        if(lines == null)
            return ErrorCode.OTHER_ERROR;

        // Edit the entry we want to edit
        String[] oldData = lines.get(entry).split(",");
        lines.set(entry, oldData[0] + "," + oldData[1] + "," + oldData[2] + "," + newReason);

        // Write changes back to file
        try {
            Util.writeSmallTextFile(file, lines);
        } catch (Exception e) {
            System.out.println("Couldn't write user history for user " + m.getId() + "(" + m.getUser().getAsTag() + ") in server " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }
}
