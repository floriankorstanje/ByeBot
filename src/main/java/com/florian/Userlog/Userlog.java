package com.florian.Userlog;

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
        String folder = Util.getGuildFolder(g);
        String file = folder + Vars.userlogFile;

        // Remove "Event" from the action
        action = action.replace("Event", "");

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
        // Entries are formatted as following: userid,epoch-time,action
        lines.add(m.getId() + "," + time + "," + action);

        // Clear old entries
        lines = clearOldEntries(lines, m);

        // Write back to the file
        try {
            Util.writeSmallTextFile(file, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> clearOldEntries(List<String> file, Member m) {
        // Clear entries if the user has more then max entries
        // Oldest entries are at the top of the file so if we start searching from the bottom and we reach the max, we delete the oldest entries
        int entries = 0;
        for (int i = file.size() - 1; i >= 0; i--) {
            // Check if the entry is about the specified user
            if (file.get(i).split(",")[0].equals(m.getId())) {
                // Increment entries and remove entries from the file if they're above the maximum
                entries++;
                if (entries > Vars.maxUserlogEntries)
                    file.remove(i);
            }
        }

        // Return the new list
        return file;
    }
}
