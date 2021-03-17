package com.florian.GuildConfig;

import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GuildConfig {
    private static String cmdCounterId = "commandCounter";
    private static String prefixId = "prefix";

    public static ErrorCode setPrefix(Guild g, String prefix) {
        ErrorCode error = setValue(g, prefixId, prefix);

        // Return the error
        return error;
    }

    public static void incrementCommandCounter(Guild g) {
        setValue(g, cmdCounterId, String.valueOf(Integer.parseInt(getValue(g, cmdCounterId)) + 1));
    }

    public static String getPrefix(Guild g) {
        String prefix = getValue(g, prefixId);

        // If prefix is null, return the default one
        if(prefix == null)
            return Vars.botPrefix;

        // Return prefix
        return prefix;
    }

    public static int getCommandCounter(Guild g) {
        return Integer.parseInt(getValue(g, cmdCounterId));
    }

    private static String getValue(Guild g, String key) {
        // Get file location
        String folder = Util.getGuildFolder(g);
        String file = folder + Vars.guildConfigFile;

        // Check if the guild folder exists
        File guildsFolder = new File(folder);
        if (!guildsFolder.exists()) {
            boolean success = guildsFolder.mkdirs();

            // If it wasn't successful, quit
            if (!success) {
                System.out.println("Unable to create guilds folder. Quitting.");
                return "";
            }
        }

        // Create config file if it doesn't exist
        if(!new File(file).exists()) {
            try {
                createDefaultConfig(file);
            } catch (Exception e) {
                System.out.println("Couldn't create guild config file for guild " + g.getId());
                return "";
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
            return "";
        }

        // Get element
        Element element = (Element) document.getElementsByTagName(key).item(0);

        // Return value
        return element.getTextContent();
    }

    private static ErrorCode setValue(Guild g, String key, String value) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.guildConfigFile;

        if(!new File(file).exists()) {
            try {
                createDefaultConfig(file);
            } catch (Exception e) {
                System.out.println("Couldn't create guild config file for guild " + g.getId());
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

        // Get element
        Element element = (Element) document.getElementsByTagName(key).item(0);

        // Edit value
        element.setTextContent(value);

        // Write changes back to file
        Util.writeXml(file, document);

        // Return success
        return ErrorCode.SUCCESS;
    }

    private static boolean createDefaultConfig(String filename) {
        try {
            // Create file if it doesn't exist
            if(!new File(filename).exists())
                Files.createFile(Paths.get(filename));

            // Create document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Add root element
            Element root = document.createElement("guildConfig");
            document.appendChild(root);

            // Add prefix entry
            Element prefix = document.createElement(prefixId);
            prefix.setTextContent(Vars.botPrefix);
            root.appendChild(prefix);

            // Add commands executed counter
            Element counter = document.createElement(cmdCounterId);
            counter.setTextContent("0");
            root.appendChild(counter);

            // Write to file
            Util.writeXml(filename, document);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
