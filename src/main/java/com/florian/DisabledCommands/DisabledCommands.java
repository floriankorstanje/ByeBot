package com.florian.DisabledCommands;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.Moderation.DisabledcommandsCommand;
import com.florian.Commands.Utility.HelpCommand;
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
import java.util.List;

public class DisabledCommands {
    public static ErrorCode disableCommand(Guild g, BaseCommand command) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.disabledCommandsFile;

        // Check if the file exists, if not, create one
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                Log.log("Unable to create disabled commands file for guild " + g.getId());
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Get original list of commands
        List<String> commands;
        try {
            commands = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read disabled commands file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Add command to list
        commands.add(command.command);

        // Write changes back to file
        try {
            Util.writeFile(file, commands);
        } catch (IOException e) {
            Log.log("Unable to write disabled commands file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode enableCommand(Guild g, BaseCommand command) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.disabledCommandsFile;

        // Check if the file exists, if not, return success
        if (!new File(file).exists())
            return ErrorCode.SUCCESS;

        // Get original list of commands
        List<String> commands;
        try {
            commands = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read disabled commands file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // List to write back to file
        List<String> writeBack = new ArrayList<>();

        // Loop through all the commands and only add them back to the file if we don't want to remove them
        for (String cmd : commands) {
            if (!cmd.equalsIgnoreCase(command.command))
                writeBack.add(cmd);
        }

        // Write changes back to file
        try {
            Util.writeFile(file, writeBack);
        } catch (IOException e) {
            Log.log("Unable to write disabled commands file for guild " + g.getId());
            return ErrorCode.OTHER_ERROR;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    public static Pair<String[], ErrorCode> getDisabledCommands(Guild g) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.disabledCommandsFile;

        // Check if the file exists, if not, create one
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                Log.log("Unable to create disabled commands file for guild " + g.getId());
                return Pair.of(new String[]{}, ErrorCode.OTHER_ERROR);
            }
        }

        // Get list of commands
        List<String> commands;
        try {
            commands = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read disabled commands file for guild " + g.getId());
            return Pair.of(new String[]{}, ErrorCode.OTHER_ERROR);
        }

        // Return the words
        return Pair.of(commands.toArray(new String[0]), ErrorCode.SUCCESS);
    }

    public static boolean isDisabled(Guild g, BaseCommand command) {
        // Get file location
        String file = Util.getGuildFolder(g) + Vars.disabledCommandsFile;

        // Check if the file exists, if not, return false
        if (!new File(file).exists())
            return false;

        // Get list of commands
        List<String> commands;
        try {
            commands = Util.readFile(file);
        } catch (IOException e) {
            Log.log("Unable to read disabled commands file for guild " + g.getId());
            return false;
        }

        return commands.contains(command.command);
    }

    public static List<String> getCannotBeDisabled() {
        List<String> list = new ArrayList<>();
        list.add(new HelpCommand().command);
        list.add(new DisabledcommandsCommand().command);

        return list;
    }
}
