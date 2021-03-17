package com.florian.UserHistory;

import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserHistory {
    public static ErrorCode addEntry(Guild g, String user, Member executor, OffenseType type, String historyId, String reason) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.historyFile;

        if(!new File(file).exists()) {
            try {
                Util.createXmlFile(file, "history");
            } catch (Exception e) {
                System.out.println("Couldn't create history file for guild " + g.getId());
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
        for(int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if(element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Save user element
        Element element;

        // Check if an element was found, if not create
        if(userIndex == -1) {
            Element toAdd = document.createElement("entries");
            toAdd.setAttribute("user", user);
            element = (Element) document.getElementsByTagName("history").item(0).appendChild(toAdd);
        } else {
            element = (Element) entries.item(userIndex);
        }

        // Create entry and add all the info
        Element entry = document.createElement("entry");
        entry.setAttribute("id", historyId);
        entry.setAttribute("executor", executor.getId());
        entry.setAttribute("time", String.valueOf(Instant.now().toEpochMilli()));
        entry.setAttribute("type", type.toString());
        entry.setAttribute("reason", reason.trim());

        // Add entry to document
        element.appendChild(entry);

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode removeEntry(Guild g, String user, String id) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.historyFile;

        if(!new File(file).exists())
            return ErrorCode.NO_USER_HISTORY;

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
        for(int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if(element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Check if the user has history
        if(userIndex == -1)
            return ErrorCode.NO_USER_HISTORY;

        // Get a list of all the user's entries
        NodeList userEntries = entries.item(userIndex).getChildNodes();

        // Loop through them and delete one if it matches the ID
        boolean deleted = false;
        for(int i = 0; i < userEntries.getLength(); i++) {
            Node node = userEntries.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if(element.getAttribute("id").equals(id)) {
                    // Make sure no empty lines are left behind
                    element.getParentNode().setTextContent("");

                    entries.item(userIndex).removeChild(node);
                    deleted = true;
                    break;
                }
            }
        }

        // Check if we actually deleted an entry
        if(!deleted)
            return ErrorCode.UNKNOWN_ENTRY;

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode editEntry(Guild g, String user, String id, String newReason) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.historyFile;

        if(!new File(file).exists())
            return ErrorCode.NO_USER_HISTORY;

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

        // Check if the user has history
        if (userIndex == -1)
            return ErrorCode.NO_USER_HISTORY;

        // Get a list of all the user's entries
        NodeList userEntries = entries.item(userIndex).getChildNodes();

        // Loop through them and edit one if it matches the ID
        boolean edited = false;
        for (int i = 0; i < userEntries.getLength(); i++) {
            Node node = userEntries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("id").equals(id)) {
                    element.setAttribute("reason", newReason);
                    edited = true;
                    break;
                }
            }
        }

        // Check if we actually edited an entry
        if (!edited)
            return ErrorCode.UNKNOWN_ENTRY;

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static Pair<UserHistoryEntry[], ErrorCode> getAllHistory(Guild g, String user) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.historyFile;

        if(!new File(file).exists())
            return Pair.of(new UserHistoryEntry[] {}, ErrorCode.NO_USER_HISTORY);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(new UserHistoryEntry[] {}, ErrorCode.OTHER_ERROR);
        }

        // Get all history entries
        NodeList entries = document.getElementsByTagName("entries");

        // Save index of user
        int userIndex = -1;

        // Get one for the current user
        for(int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if(element.getAttribute("user").equals(user)) {
                    userIndex = i;
                    break;
                }
            }
        }

        // Check if the user has history
        if(userIndex == -1)
            return Pair.of(new UserHistoryEntry[] {}, ErrorCode.NO_USER_HISTORY);

        // Get a list of all the user's entries
        NodeList userEntries = entries.item(userIndex).getChildNodes();

        // Create list of UserHistoryEntry to return
        List<UserHistoryEntry> list = new ArrayList<>();

        // Loop through them and add them to the list of entries
        for(int i = 0; i < userEntries.getLength(); i++) {
            Node node = userEntries.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String executor = element.getAttribute("executor");
                long time = Long.parseLong(element.getAttribute("time"));
                String type = element.getAttribute("type");
                String reason = element.getAttribute("reason");
                String id = element.getAttribute("id");

                list.add(new UserHistoryEntry(executor, time, type, reason, id));
            }
        }

        // Return success
        return Pair.of(list.toArray(new UserHistoryEntry[0]), ErrorCode.SUCCESS);
    }
}
