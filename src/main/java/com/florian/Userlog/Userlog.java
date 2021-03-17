package com.florian.Userlog;

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

public class Userlog {
    public static void addEvent(Guild g, Member m, String action) {
        // First make sure none of the arguments are null
        if (g == null || m == null || action == null)
            return;

        // Get the file location
        String file = Util.getGuildFolder(g) + Vars.userlogFile;

        // Remove "Event" from the action
        action = action.replaceAll("Event", "");

        // Remove "Guild" from the action
        action = action.replaceAll("Guild", "");

        // Replace "Received" with "Sent" (MessageReceived -> MessageSent)
        action = action.replaceAll("Received", "Sent");

        // Check if the file for this guild is here
        if(!new File(file).exists()) {
            try {
                Util.createXmlFile(file, "userlog");
            } catch (Exception e) {
                System.out.println("Couldn't create history file for guild " + g.getId());
                return;
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
            return;
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
                if(element.getAttribute("user").equals(m.getId())) {
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
            toAdd.setAttribute("user", m.getId());
            element = (Element) document.getElementsByTagName("userlog").item(0).appendChild(toAdd);
        } else {
            element = (Element) entries.item(userIndex);
        }

        // Create entry and add all the info
        Element entry = document.createElement("entry");
        entry.setAttribute("time", String.valueOf(Instant.now().toEpochMilli()));
        entry.setAttribute("action", action);

        // Add entry to document
        element.appendChild(entry);

        // Remove entries if there are too many
        while(element.getChildNodes().getLength() > Vars.maxUserlogEntries)
            element.removeChild(element.getFirstChild());

        // Write changes back to file
        Util.writeXml(file, document);
    }

    public static Pair<UserlogEntry[], ErrorCode> getEntries(Guild g, String user) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.userlogFile;

        if(!new File(file).exists())
            return Pair.of(new UserlogEntry[] {}, ErrorCode.NO_USER_LOG);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(new UserlogEntry[] {}, ErrorCode.OTHER_ERROR);
        }

        // Get all log entries
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

        // Check if the user has logs
        if(userIndex == -1)
            return Pair.of(new UserlogEntry[] {}, ErrorCode.NO_USER_LOG);

        // Get a list of all the user's entries
        NodeList userEntries = entries.item(userIndex).getChildNodes();

        // Create list of UserlogEntry to return
        List<UserlogEntry> list = new ArrayList<>();

        // Loop through them and add them to the list of entries
        for(int i = userEntries.getLength() - 1; i >= 0; i--) {
            Node node = userEntries.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String action = element.getAttribute("action");
                long time = Long.parseLong(element.getAttribute("time"));

                list.add(new UserlogEntry(time, action));
            }
        }

        // Return success
        return Pair.of(list.toArray(new UserlogEntry[0]), ErrorCode.SUCCESS);
    }
}
