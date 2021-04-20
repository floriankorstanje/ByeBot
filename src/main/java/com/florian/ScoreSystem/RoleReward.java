package com.florian.ScoreSystem;

import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RoleReward {
    public static ErrorCode addRoleReward(Guild g, int score, String role) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.roleRewardsFile;

        if (!new File(file).exists()) {
            try {
                Util.createXmlFile(file, "rolerewards");
            } catch (Exception e) {
                Log.log("Couldn't create rolerewards file");
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

        // Make sure there isn't too many role rewards
        if (document.getElementsByTagName("rolerewards").item(0).getChildNodes().getLength() > Vars.maxRoleRewardEntries)
            return ErrorCode.TOO_MANY_ROLE_REWARDS;

        // Create entry and add all the info
        Element entry = document.createElement("entry");
        entry.setAttribute("score", String.valueOf(score));
        entry.setAttribute("role", role);
        entry.setAttribute("id", Long.toHexString(Instant.now().toEpochMilli()));

        // Add entry to document
        document.getElementsByTagName("rolerewards").item(0).appendChild(entry);

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static Pair<RoleRewardEntry, ErrorCode> removeRoleReward(Guild g, String id) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.roleRewardsFile;

        if (!new File(file).exists())
            return Pair.of(null, ErrorCode.NO_ROLE_REWARDS);

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

        // Get a list of all the entries
        NodeList userEntries = document.getElementsByTagName("rolerewards").item(0).getChildNodes();

        // Loop through them and delete one if it matches the ID
        RoleRewardEntry deleted = null;
        for (int i = 0; i < userEntries.getLength(); i++) {
            Node node = userEntries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("id").equals(id)) {
                    int score = Integer.parseInt(element.getAttribute("score"));
                    String role = element.getAttribute("role");
                    String roleRewardId = element.getAttribute("id");

                    // Save all the values of the deleted entry
                    deleted = new RoleRewardEntry(role, score, roleRewardId);

                    // Remove the element
                    document.getElementsByTagName("rolerewards").item(0).removeChild(node);

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

    public static Pair<RoleRewardEntry[], ErrorCode> getRoleRewards(Guild g) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.roleRewardsFile;

        if (!new File(file).exists())
            return Pair.of(new RoleRewardEntry[]{}, ErrorCode.NO_ROLE_REWARDS);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(new RoleRewardEntry[]{}, ErrorCode.OTHER_ERROR);
        }

        // Get all reminders
        NodeList entries = document.getElementsByTagName("entry");

        // Create list of RoleRewardEntry to return
        List<RoleRewardEntry> list = new ArrayList<>();

        // Loop through them and add them to the list of entries
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                int score = Integer.parseInt(element.getAttribute("score"));
                String role = element.getAttribute("role");
                String id = element.getAttribute("id");

                list.add(new RoleRewardEntry(role, score, id));
            }
        }

        // Return success
        return Pair.of(list.toArray(new RoleRewardEntry[0]), ErrorCode.SUCCESS);
    }
}
