package com.florian.Commands;

import com.florian.ErrorCode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class BaseCommand {
    public String command;
    public String description;
    public String advancedDescription;
    public String arguments;
    public Permission permission;
    public List<String> aliases;
    public List<String> examples;
    public CommandType commandType;
    public boolean requiredArguments;

    public BaseCommand() {
        // Command is just the command
        command = "command";

        // Description briefly explains what the command does
        description = "If this shows up, I made a big oopsie and forgot to give one of the commands a description.";

        // Advanced description explains what the command does
        advancedDescription = "none";

        // Arguments are the arguments the command needs
        arguments = "";

        // Permission is the permission a user needs to execute the command
        permission = null;

        // Aliases are different commands to execute one command
        aliases = new ArrayList<>();

        // Examples are some examples for a command if it's hard to use
        examples = new ArrayList<>();

        // userType is used to differentiate between mod, owner and normal commands
        commandType = CommandType.NOT_SET;

        // requiredArguments is true if the command requires one or more arguments
        requiredArguments = false;
    }

    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // This is never supposed to happen
        return ErrorCode.UNINITIALIZED_COMMAND;
    }
}
