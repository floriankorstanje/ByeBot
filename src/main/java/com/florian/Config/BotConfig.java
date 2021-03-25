package com.florian.Config;

import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BotConfig {
    private static final String cmdCounterId = "commandCounter";
    private static final String tokenId = "token";

    public static void incrementCommandCounter() {
        setValue(cmdCounterId, String.valueOf(Integer.parseInt(getValue(cmdCounterId)) + 1));
    }

    public static String getToken() {
        String token = getValue(tokenId);

        // If token is null, return NO TOKEN
        if (token == null)
            return "NO TOKEN";

        // Return token
        return token;
    }

    public static int getCommandCounter() {
        return Integer.parseInt(getValue(cmdCounterId));
    }

    private static String getValue(String key) {
        // Get file location
        String file = Vars.botConfigFile;

        // Create config file if it doesn't exist
        if (!new File(file).exists()) {
            try {
                createDefaultConfig(file);
            } catch (Exception e) {
                Log.log("Couldn't create bot config file");
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

    private static ErrorCode setValue(String key, String value) {
        // Get file location
        String file = Vars.botConfigFile;

        // Create bot config if it doesn't exist
        if (!new File(file).exists()) {
            try {
                createDefaultConfig(file);
            } catch (Exception e) {
                Log.log("Couldn't create bot config file");
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
            if (!new File(filename).exists())
                Files.createFile(Paths.get(filename));

            // Create document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            // Add root element
            Element root = document.createElement("botConfig");
            document.appendChild(root);

            // Add token entry
            Element prefix = document.createElement(tokenId);
            prefix.setTextContent("TOKEN HERE");
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
