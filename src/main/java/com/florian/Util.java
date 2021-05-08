package com.florian;

import com.florian.Commands.BaseCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.ocpsoft.prettytime.PrettyTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
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

    public static EmbedBuilder defaultEmbed(boolean footer) {
        // This creates an embed with a default footer and color
        EmbedBuilder embed = new EmbedBuilder();

        // Set the default values
        embed.setColor(Vars.color);
        if (footer)
            embed.setFooter(Vars.appInfo.getName() + " made with ❤ by " + Vars.botOwner.getName() + " - v" + Vars.version, Vars.botOwner.getAvatarUrl());

        // Return the embed
        return embed;
    }

    public static EmbedBuilder defaultEmbed() {
        // Creates an embed with footer
        return defaultEmbed(true);
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

    public static String formatDateTime(Date date) {
        // SimpleDateFormat to get the date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss zz");

        return "`" + formatter.format(date) + "`";
    }

    public static String formatDateAgo(Date date) {
        // SimpleDateFormat to get the date
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // PrettyTime to get time ago
        PrettyTime pretty = new PrettyTime();

        // Return result
        return "`" + formatter.format(date) + "` (" + pretty.format(date) + ")";
    }

    public static String getUptime() {
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        return formatTime(rb.getUptime());
    }

    public static String formatTime(long millis) {
        final long days = TimeUnit.MILLISECONDS.toDays(millis);
        final long hours = TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        return String.format("%d Days, %d Hours, %d Minutes, %d Seconds", days, hours, minutes, seconds);
    }

    public static String generateId() {
        return Long.toHexString(Instant.now().toEpochMilli());
    }

    public static boolean createXmlFile(String filename, String rootTag) {
        try {
            // Create file if it doesn't exist
            if (!new File(filename).exists())
                Files.createFile(Paths.get(filename));

            // Create document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Add root element
            Element root = document.createElement(rootTag);
            document.appendChild(root);

            // Write to file
            writeXml(filename, document);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean writeXml(String filename, Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder();
    }

    public static List<Member> removeBots(List<Member> list) {
        List<Member> newList = new ArrayList<>();
        for (Member m : list) {
            if (!m.getUser().isBot())
                newList.add(m);
        }

        return newList;
    }

    public static List<String> readFile(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public static void writeFile(String fileName, List<String> lines) throws IOException {
        Path path = Paths.get(fileName);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public static boolean validUser(Guild g, String user) {
        try {
            g.retrieveMemberById(user).complete();
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    public static BaseCommand getCommandByName(String name) {

    }
}
