package com.florian.Reminders;

import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Reminders {
    public static ErrorCode addReminder(Guild g, TextChannel channel, String id, String user, long time, String reason) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        if (!new File(file).exists()) {
            try {
                Util.createXmlFile(file, "reminders");
            } catch (Exception e) {
                Log.log("Couldn't create reminders file for guild " + g.getId());
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return ErrorCode.OTHER_ERROR;
        }

        // Get all history entries
        NodeList entries = document.getElementsByTagName("entries");

        // Save index of user
        int userIndex = -1;

        // Get one for the current user=
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Save user element
        Element element;

        // Check if an element was found, if not create
        if (userIndex == -1) {
            Element toAdd = document.createElement("entries");
            toAdd.setAttribute("user", user);
            element = (Element) document.getElementsByTagName("reminders").item(0).appendChild(toAdd);
        } else {
            element = (Element) entries.item(userIndex);
        }

        // Check if there isn't too many reminders
        if(element.getChildNodes().getLength() >= Vars.maxReminderEntries)
            return ErrorCode.TOO_MANY_REMINDERS;

        // Create entry and add all the info
        Element entry = document.createElement("entry");
        entry.setAttribute("id", id);
        entry.setAttribute("channel", channel.getId());
        entry.setAttribute("time", String.valueOf(time));
        entry.setAttribute("reason", reason.trim());

        // Add entry to document
        element.appendChild(entry);

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static Pair<ReminderEntry, ErrorCode> removeReminder(Guild g, String user, String id) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        if (!new File(file).exists())
            return Pair.of(null, ErrorCode.NO_REMINDERS);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(null, ErrorCode.OTHER_ERROR);
        }

        // Get all history entries
        NodeList entries = document.getElementsByTagName("entries");

        // Save index of user
        int userIndex = -1;

        // Get one for the current user=
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Check if the user has history
        if (userIndex == -1)
            return Pair.of(null, ErrorCode.NO_REMINDERS);

        // Get a list of all the user's entries
        NodeList userEntries = entries.item(userIndex).getChildNodes();

        // Loop through them and delete one if it matches the ID
        ReminderEntry deleted = null;
        for (int i = 0; i < userEntries.getLength(); i++) {
            Node node = userEntries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("id").equals(id)) {
                    String channel = element.getAttribute("channel");
                    long time = Long.parseLong(element.getAttribute("time"));
                    String reason = element.getAttribute("reason");
                    String reminderId = element.getAttribute("id");

                    // Save all the values of the deleted entry
                    deleted = new ReminderEntry(reminderId, user, channel, time, reason);

                    // Remove the element
                    entries.item(userIndex).removeChild(node);

                    break;
                }
            }
        }

        // Check if we actually deleted an entry
        if (deleted == null)
            return Pair.of(null, ErrorCode.UNKNOWN_ENTRY);

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return Pair.of(deleted, ErrorCode.SUCCESS);
    }

    public static Pair<ReminderEntry[], ErrorCode> getReminders(Guild g, String user) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        if (!new File(file).exists())
            return Pair.of(new ReminderEntry[]{}, ErrorCode.NO_REMINDERS);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(new ReminderEntry[]{}, ErrorCode.OTHER_ERROR);
        }

        // Get all reminders
        NodeList entries = document.getElementsByTagName("entries");

        // Save index of user
        int userIndex = -1;

        // Get one for the current user
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Check if the user has reminders
        if (userIndex == -1)
            return Pair.of(new ReminderEntry[]{}, ErrorCode.NO_REMINDERS);

        // Get a list of all the user's entries
        NodeList userEntries = entries.item(userIndex).getChildNodes();

        // Create list of ReminderEntry to return
        List<ReminderEntry> list = new ArrayList<>();

        // Loop through them and add them to the list of entries
        for (int i = 0; i < userEntries.getLength(); i++) {
            Node node = userEntries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String channel = element.getAttribute("channel");
                long time = Long.parseLong(element.getAttribute("time"));
                String reason = element.getAttribute("reason");
                String id = element.getAttribute("id");

                list.add(new ReminderEntry(id, user, channel, time, reason));
            }
        }

        // Return success
        return Pair.of(list.toArray(new ReminderEntry[0]), ErrorCode.SUCCESS);
    }

    public static Pair<Integer, ErrorCode> clearReminders(Guild g, String user) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.remindersFile;

        if (!new File(file).exists())
            return Pair.of(0, ErrorCode.NO_REMINDERS);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(0, ErrorCode.OTHER_ERROR);
        }

        // Get all reminders
        NodeList entries = document.getElementsByTagName("entries");

        // Save index of user
        int userIndex = -1;

        // Get one for the current user
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Check if the user has reminders
        if (userIndex == -1)
            return Pair.of(0, ErrorCode.NO_REMINDERS);

        // Get count of user reminder entries
        int entryCount = entries.item(userIndex).getChildNodes().getLength();

        // Remove all reminders
        entries.item(userIndex).setTextContent("");

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return Pair.of(entryCount, ErrorCode.SUCCESS);
    }
}
