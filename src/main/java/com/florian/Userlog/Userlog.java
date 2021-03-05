package com.florian.Userlog;

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

public class Userlog {
    public static void addEvent(Guild g, Member m, String action) {
        // First make sure none of the arguments are null
        if (g == null || m == null || action == null)
            return;

        // Get the guilds folder and file
        String folder = Util.getGuildFolder(g) + Vars.userlogFolder;
        String file = folder + m.getId();

        // Remove "Event" from the action
        action = action.replaceAll("Event", "");

        // Remove "Guild" from the action
        action = action.replaceAll("Guild", "");

        // Replace "Received" with "Sent" (MessageReceived -> MessageSent)
        action = action.replaceAll("Received", "Sent");

        // Check if the guild has a folder
        File server = new File(folder);
        if (!server.exists()) {
            boolean success = server.mkdirs();

            // If it couldn't create the folder, quit
            if (!success) {
                System.out.println("Couldn't create server folder for guild " + g.getId() + " (" + g.getName() + ")");
                return;
            }
        }

        // Check if the file for this guild is here
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get the full file
        List<String> lines = null;
        try {
            lines = Util.readSmallTextFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make sure lines isn't nul
        if (lines == null)
            return;

        // Get current epoch time (ms)
        long time = Instant.now().toEpochMilli();

        // Add entry to the file
        // Entries are formatted as following: epoch-time,action
        lines.add(time + "," + action);

        // Clear old entries
        clearOldEntries(lines);

        // Write back to the file
        try {
            Util.writeSmallTextFile(file, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UserlogEntries getEntries(Guild g, Member m) {
        // Get the guilds folder and file
        String folder = Util.getGuildFolder(g) + Vars.userlogFolder;
        String file = folder + m.getId();

        // Initialize class to return if an error occurred
        UserlogEntries entries = new UserlogEntries(new UserlogEntry[] {}, ErrorCode.SUCCESS);

        // Check if the guild has a folder
        File server = new File(folder);
        if (!server.exists()) {
            boolean success = server.mkdirs();

            // If it couldn't create the folder, quit
            if (!success) {
                System.out.println("Couldn't create server folder for guild " + g.getId() + " (" + g.getName() + ")");
                entries.setError(ErrorCode.OTHER_ERROR);
                return entries;
            }
        }

        // Check if the file for this guild is here
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Get the full file
        List<String> lines = null;
        try {
            lines = Util.readSmallTextFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Make sure lines isn't nul
        if (lines == null) {
            entries.setError(ErrorCode.OTHER_ERROR);
            return entries;
        }

        // Get all the entries
        // Loop needs to decrement so newest entry is top of the list
        UserlogEntry[] list = new UserlogEntry[lines.size()];
        for(int i = lines.size() - 1; i >= 0; i--) {
            String[] data = lines.get(i).split(",");
            long time = Long.parseLong(data[0]);
            String action = data[1];

            list[lines.size() - i - 1] = new UserlogEntry(time, action);
        }

        // Add all the entries to return class
        entries.setEntries(list);

        // Return success
        return entries;
    }

    private static void clearOldEntries(List<String> file) {
        // Clear entries if the user has more then max entries
        while(file.size() > Vars.maxUserlogEntries) {
            // Remove the first element in the list. This is the oldest event
            file.remove(0);
        }
    }
}