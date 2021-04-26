package com.florian.WordBlacklist;

import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordBlacklist {
    public static ErrorCode addBlacklistedWords(Guild g, String[] wordsArray) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.blacklistedWordsFile;

        // Check if the file exists, if not, create one
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                Log.log("Unable to create blacklisted words file for guild " + g.getId());
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Get original list of words
        List<String> words;
        try {
            words = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read blacklisted words file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Loop through all the words we want to add and then add them
        for (String word : wordsArray) {
            // Check if the word isn't already in the list. If it's already added, skip to the next word
            if (words.contains(word))
                continue;

            // Add the word we want to add to the list
            words.add(word);
        }

        // Write changes back to file
        try {
            Util.writeFile(file, words);
        } catch (IOException e) {
            Log.log("Unable to write blacklisted words file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode removeBlacklistedWords(Guild g, String[] wordsArray) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.blacklistedWordsFile;

        // Check if the file exists, if not, return success
        if (!new File(file).exists())
            return ErrorCode.SUCCESS;

        // Get original list of words
        List<String> words;
        try {
            words = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read blacklisted words file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // List to write back to file
        List<String> writeBack = new ArrayList<>();

        // Loop through all the words and only add them back to the file if we don't want to remove them
        for (String word : words) {
            if (!Arrays.asList(wordsArray).contains(word))
                writeBack.add(word);
        }

        // Write changes back to file
        try {
            Util.writeFile(file, writeBack);
        } catch (IOException e) {
            Log.log("Unable to write blacklisted words file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Return success and the words we couldn't remove
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode clearBlacklistedWords(Guild g) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.blacklistedWordsFile;

        // Check if the file exists, if not, return success
        if (!new File(file).exists())
            return ErrorCode.SUCCESS;

        // Delete the file
        try {
            Files.delete(Paths.get(file));
        } catch (IOException e) {
            Log.log("Unable to delete blacklisted words file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static Pair<String[], ErrorCode> getBlacklistedWords(Guild g) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.blacklistedWordsFile;

        // Check if the file exists, if not, create one
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                Log.log("Unable to create blacklisted words file for guild " + g.getId());
                return Pair.of(new String[]{}, ErrorCode.OTHER_ERROR);
            }
        }

        // Get list of words
        List<String> words;
        try {
            words = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read blacklisted words file for guild " + g.getId());
            return Pair.of(new String[]{}, ErrorCode.OTHER_ERROR);
        }

        // Return the words
        return Pair.of(words.toArray(new String[0]), ErrorCode.SUCCESS);
    }

    public static boolean checkMessage(Guild g, String message) {
        // Get all blacklisted words from the guild
        Pair<String[], ErrorCode> blacklistedWords = getBlacklistedWords(g);

        // Make sure getBlacklistedWords succeeded
        if (blacklistedWords.getRight() != ErrorCode.SUCCESS)
            return false;

        // Check message for words
        for (String word : blacklistedWords.getLeft()) {
            // Make sure it isn't an empty line
            if (word.length() < 1)
                continue;

            if (message.toLowerCase().contains(word.toLowerCase().trim()))
                return true;
        }

        // Return false by default
        return false;
    }
}
