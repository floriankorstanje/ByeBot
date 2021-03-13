package com.florian.Reminders;

import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserReminders {
    public static ErrorCode addReminder(Guild g, TextChannel channel, String user, long time, String reason) {
        // Get file for reminders
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        // Check if the file exists
        if (!new File(file).exists()) {
            try {
                // File doesn't exist, so create it
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                System.out.println("Couldn't create reminders file for guild " + g.getId() + " (" + g.getName() + ")");
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Get all lines in the file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read reminders for guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Make sure lines isn't null
        if (lines == null)
            return ErrorCode.OTHER_ERROR;

        // Generate history ID
        String reminderId = Long.toHexString(Instant.now().toEpochMilli());

        // Add this entry to the list
        // Entries are formatted as following: reminder-id,user-id,channel-id,reminder-time,reminder-reason
        lines.add(reminderId + "," + user + "," + channel.getId() + "," + time + "," + reason);

        // Write changes back to file
        try {
            Util.writeFile(file, lines);
        } catch (Exception e) {
            System.out.println("Couldn't write reminder for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return ErrorCode.OTHER_ERROR;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static Pair<ReminderEntry, ErrorCode> removeReminder(Guild g, String user, String id) {
        // Get file for reminders
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        // Check if the file exists
        if (!new File(file).exists())
            return Pair.of(null, ErrorCode.NO_REMINDERS);

        // Read all reminders in file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read reminders for guild " + g.getId() + " (" + g.getName() + ")");
            return Pair.of(null, ErrorCode.OTHER_ERROR);
        }

        // Make sure lines isn't null
        if (lines == null)
            return Pair.of(null, ErrorCode.OTHER_ERROR);

        // Check if there is any history
        if (lines.size() == 0)
            return Pair.of(null, ErrorCode.NO_REMINDERS);

        // Loop through all lines and remove the right one
        ReminderEntry entry = null;
        for(int i = 0; i < lines.size(); i++) {
            // Get the reminder at that line
            String[] data = lines.get(i).split(",");
            String reminderId = data[0];
            String userId = data[1];
            String channelId = data[2];
            long time = Long.parseLong(data[3]);
            String reason = data[4];

            // Remove the entry if it's the right ID and user
            if(reminderId.equalsIgnoreCase(id) && userId.equals(user)) {
                lines.remove(i);
                entry = new ReminderEntry(reminderId, userId, channelId, time, reason);
            }
        }

        // Check if an entry was removed
        if(entry == null)
            return Pair.of(null, ErrorCode.UNKNOWN_ENTRY);

        // Write back to the file
        try {
            Util.writeFile(file, lines);
        } catch (IOException ex) {
            System.out.println("Couldn't write reminder for user " + user + " in guild " + g.getId() + " (" + g.getName() + ")");
            return Pair.of(null, ErrorCode.OTHER_ERROR);
        }

        // Return success
        return Pair.of(entry, ErrorCode.SUCCESS);
    }

    public static Pair<ReminderEntry[], ErrorCode> getReminders(Guild g, String user) {
        // Get file for reminders
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        // Check if the file exists
        if (!new File(file).exists())
            return Pair.of(new ReminderEntry[]{}, ErrorCode.NO_REMINDERS);

        // Read all reminders in file
        List<String> lines;
        try {
            lines = Util.readFile(file);
        } catch (IOException e) {
            System.out.println("Couldn't read reminders for guild " + g.getId() + " (" + g.getName() + ")");
            return Pair.of(new ReminderEntry[]{}, ErrorCode.OTHER_ERROR);
        }

        // Make sure lines isn't null
        if (lines == null)
            return Pair.of(new ReminderEntry[]{}, ErrorCode.OTHER_ERROR);

        // Check if there is any history
        if (lines.size() == 0)
            return Pair.of(new ReminderEntry[]{}, ErrorCode.NO_REMINDERS);

        // Get all the entries
        List<ReminderEntry> list = new ArrayList<>();
        for (String line : lines) {
            String[] data = line.split(",");
            String reminderId = data[0];
            String userId = data[1];
            String channelId = data[2];
            long time = Long.parseLong(data[3]);
            String reason = data[4];

            // Only add the reminder to the list if the ID of the reminder is the same as the user's ID
            if (userId.equals(user))
                list.add(new ReminderEntry(reminderId, userId, channelId, time, reason));
        }

        // If there's no entries in the list, return NO_REMINDERS
        if (list.size() == 0)
            return Pair.of(new ReminderEntry[]{}, ErrorCode.NO_REMINDERS);

        // Return the entries
        return Pair.of(list.toArray(new ReminderEntry[0]), ErrorCode.SUCCESS);
    }
}
