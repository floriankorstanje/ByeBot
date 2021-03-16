package com.florian.UserHistory;

import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;

public class UserHistory {
    public static ErrorCode addEntry(Guild g, String user, Member executor, OffenseType type, String historyId, String entry) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + user;

        // Check if folder exists
        File historyFolder = new File(folder);
        if (!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if (!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Check if file exists
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Make sure lines isn't null
        if (lines == null)
            return ErrorCode.OTHER_ERROR;


        // Get current epoch time (ms)
        long time = Instant.now().toEpochMilli();

        // Add this entry to the list
        // Entries are formatted as following: history-id,executor-id,epoch-time,offense,entry
        lines.add(historyId + "," + executor.getId() + "," + time + "," + type.toString() + "," + entry);

        // Write changes back to file
        try {
            Util.writeFile(file, lines);
        } catch (Exception e) {
            System.out.println("Couldn't write user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode removeEntry(Guild g, String user, String id) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + user;

        // Check if folder exists
        File historyFolder = new File(folder);
        if (!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if (!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }

            // If it did create, return no history because there is no history files for this guild
            return ErrorCode.NO_USER_HISTORY;
        }

        // If the file doesn't exist, there is also no history
        if (!new File(file).exists())
            return ErrorCode.NO_USER_HISTORY;

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }
        // Make sure lines isn't null
        if (lines == null)
            return ErrorCode.OTHER_ERROR;

        // Get the line of the entry we want to edit
        int line = getEntryLine(lines, id);

        // If getEntryLine returns -1 it couldn't file the ID
        if (line == -1)
            return ErrorCode.UNKNOWN_ENTRY;


        // Remove the entry we want to remove
        try {
            // Get the line to check if it's a ban
            String[] data = lines.get(line).split(",");
            OffenseType type = OffenseType.valueOf(data[3]);

            // If it's a ban, unban the user
            if (type == OffenseType.BAN)
                g.unban(user).complete();

            lines.remove(line);
        } catch (Exception ex) {
            // Couldn't remove entry
            return ErrorCode.UNKNOWN_ENTRY;
        }

        // Write changes back to file, if there's no entries anymore we can just delete the file
        if (lines.size() == 0) {
            boolean success = new File(file).delete();

            if (!success) {
                System.out.println("Couldn't delete empty history file for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }
        } else {
            try {
                Util.writeFile(file, lines);
            } catch (Exception e) {
                System.out.println("Couldn't write user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode editEntry(Guild g, String user, String id, String newReason) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + user;

        // Check if folder exists
        File historyFolder = new File(folder);
        if (!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if (!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }

            // If it did create, return no history because there is no history files for this guild
            return ErrorCode.NO_USER_HISTORY;
        }

        // If the file doesn't exist, there is also no history
        if (!new File(file).exists())
            return ErrorCode.NO_USER_HISTORY;

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Make sure lines isn't null
        if (lines == null)
            return ErrorCode.OTHER_ERROR;

        // Get the line of the entry we want to edit
        int line = getEntryLine(lines, id);

        // If getEntryLine returns -1 it couldn't file the ID
        if (line == -1)
            return ErrorCode.UNKNOWN_ENTRY;

        // Edit the entry we want to edit
        try {
            String[] oldData = lines.get(line).split(",");
            lines.set(line, oldData[0] + "," + oldData[1] + "," + oldData[2] + "," + newReason);
        } catch (Exception ex) {
            // Couldn't remove entry
            return ErrorCode.UNKNOWN_ENTRY;
        }

        // Write changes back to file
        try {
            Util.writeFile(file, lines);
        } catch (Exception e) {
            System.out.println("Couldn't write user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static Pair<UserHistoryEntry[], ErrorCode> getAllHistory(Guild g, String user) {
        // Get location for file
        String folder = Util.getGuildFolder(g) + Vars.historyFolder;
        String file = folder + user;

        // Check if folder exists
        File historyFolder = new File(folder);
        if (!historyFolder.exists()) {
            boolean success = historyFolder.mkdirs();

            // If it couldn't create the folder, quit
            if (!success) {
                System.out.println("Couldn't create history folder for guild " + g.getId() + " (" + g.getName() + ")");
                return Pair.of(new UserHistoryEntry[]{}, ErrorCode.OTHER_ERROR);
            }

            // If it did create, return no history because there is no history files for this guild
            return Pair.of(new UserHistoryEntry[]{}, ErrorCode.NO_USER_HISTORY);
        }

        // If the file doesn't exist, there is also no history
        if (!new File(file).exists())
            return Pair.of(new UserHistoryEntry[]{}, ErrorCode.NO_USER_HISTORY);

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException ex) {
            System.out.println("Couldn't read user history for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return Pair.of(new UserHistoryEntry[]{}, ErrorCode.OTHER_ERROR);
        }

        // Make sure lines isn't null
        if (lines == null)
            return Pair.of(new UserHistoryEntry[]{}, ErrorCode.OTHER_ERROR);

        // Check if there is any history
        if (lines.size() == 0)
            return Pair.of(new UserHistoryEntry[]{}, ErrorCode.NO_USER_HISTORY);

        // Get all the entries
        UserHistoryEntry[] list = new UserHistoryEntry[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String[] data = lines.get(i).split(",");
            String id = data[0];
            String executor = data[1];
            long date = Long.parseLong(data[2]);
            String type = data[3];
            String reason = data[4];

            list[i] = new UserHistoryEntry(executor, date, type, reason, id);
        }

        // Return the entries
        return Pair.of(list, ErrorCode.SUCCESS);
    }

    private static int getEntryLine(List<String> lines, String id) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).split(",")[0].equals(id))
                return i;
        }

        return -1;
    }
}
