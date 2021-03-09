package com.florian.Commands;

import com.florian.ErrorCode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

public class BaseCommand {
    public String command;
    public String description;
    public String arguments;
    public Permission permission;
    public List<String> aliases;
    public UserType userType;
    public boolean requiredArguments;

    public BaseCommand() {
        // Command is just the command
        command = "command";

        // Description briefly explains what the command does
        description = "If this shows up, I made a big oopsie and forgot to give one of the commands a description.";

        // Arguments are the arguments the command needs
        arguments = "";

        // Permission is the permission a user needs to execute the command
        permission = null;

        // Aliases are different commands to execute one command
        aliases = new ArrayList<>();

        // userType is used to differentiate between mod, owner and normal commands
        userType = UserType.EVERYONE;

        // requiredArguments is true if the command requires one or more arguments
        requiredArguments = false;
    }

    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // This is never supposed to happen
        return ErrorCode.UNINITIALIZED_COMMAND;
    }
}
