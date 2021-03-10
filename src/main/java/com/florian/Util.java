package com.florian;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Util {
    public static String[] removeElement(String[] arr, int index) {
        // Check if the array exists and that the index is within bounds of the array
        if (arr == null || index < 0 || index >= arr.length)
            return arr;

        // Create a new array where we copy the original array's elements into, except for the one we want to remove
        String[] newArray = new String[arr.length - 1];

        // Copy all the elements except for the one we want to remove
        for (int i = 0, k = 0; i < arr.length; i++) {
            if (i == index)
                continue;

            newArray[k++] = arr[i];
        }

        return newArray;
    }

    public static EmbedBuilder defaultEmbed() {
        // This creates an embed with a default footer and color
        EmbedBuilder embed = new EmbedBuilder();

        // Set the default values
        embed.setColor(Vars.color);
        embed.setFooter(Vars.appInfo.getName() + " made with ❤ by " + Vars.botOwner.getName(), Vars.botOwner.getAvatarUrl());

        // Return the embed
        return embed;
    }

    public static boolean containsIgnoreCase(List<String> list, String string) {
        // Loop through all the elements and check if one of the elements matches string
        for (String element : list) {
            if (element.equalsIgnoreCase(string))
                return true;
        }

        // Return false if it didn't find anything
        return false;
    }

    public static String getGuildFolder(Guild g) {
        // Return the folder where guild-specific things are stored
        return Vars.guildsFolder + g.getId() + "/";
    }

    public static String formatDate(Date date) {
        // SimpleDateFormat to get the date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");

        // PrettyTime to get time ago
        PrettyTime pretty = new PrettyTime();

        // Return result
        return "`" + formatter.format(date) + "` (" + pretty.format(date) + ")";
    }

    public static String getUptime() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        final long uptime = rb.getUptime();
        final long days = TimeUnit.MILLISECONDS.toDays(uptime);
        final long hours = TimeUnit.MILLISECONDS.toHours(uptime) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(uptime));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime));

        return String.format("%d Days, %d Hours, %d Minutes, %d Seconds", days, hours, minutes, seconds);
    }

    public static List<String> readFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static void writeFile(String fileName, List<String> lines) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
