package com.florian.Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BaseCommand {
    public String command;
    public String description;
    public String arguments;
    public Permission permission;
    public boolean requiredArguments;
    public boolean optionalArguments;

    public BaseCommand() {
        // Command is just the command
        command = "command";

        // Description briefly explains what the command does
        description = "If this shows up, I made a big oopsie and forgot to give one of the commands a description.";

        // Arguments are the arguments the command needs
        arguments = "";

        // Permission is the permission a user needs to execute the command
        permission = null;

        // requiredArguments is true if the command requires one or more arguments
        requiredArguments = false;

        // optionalArguments is true if the command has one or more optional arguments
        optionalArguments = false;
    }

    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        e.getChannel().sendMessage("Executed command " + command).queue();
        return ErrorCode.SUCCESS;
    }
}
