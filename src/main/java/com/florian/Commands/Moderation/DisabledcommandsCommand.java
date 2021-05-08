package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.DisabledCommands.DisabledCommands;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

public class DisabledcommandsCommand extends BaseCommand {
    public DisabledcommandsCommand() {
        super.command = "disabledcommands";
        super.description = "Enables or disables a command.";
        super.arguments = "[add/remove] [command]";
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.MANAGE_PERMISSIONS;
        super.examples.add("");
        super.examples.add("add hostinfo");
        super.examples.add("remove avatar");
        super.aliases.add("disablecmd");
        super.aliases.add("disabledcmds");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 0) {
            Pair<String[], ErrorCode> disabledCommands = DisabledCommands.getDisabledCommands(e.getGuild());

            // Make sure getDisabledCommands succeeded
            if (disabledCommands.getRight() != ErrorCode.SUCCESS)
                return disabledCommands.getRight();

            // Get amount of disabled commands
            int total = disabledCommands.getLeft().length;

            // Add all the commands to one string
            StringBuilder commands = new StringBuilder();
            for (String word : disabledCommands.getLeft())
                commands.append("`").append(word).append("` ");

            // Create embed to send
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Disabled commands for " + e.getGuild().getName());

            // Fill embed
            embed.addField("Total: " + total, commands.toString(), false);

            // Send embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length == 2) {
            String operation = args[0];
            String commandStr = args[1];

            // Parse commandStr to BaseCommand
            BaseCommand command;
            try {
                command = Util.getCommandByName(commandStr);
            } catch (Exception ignored) {
                return ErrorCode.UNKNOWN_COMMAND_NAME;
            }

            // Make sure the user is trying to disable a valid command
            if (Util.containsIgnoreCase(DisabledCommands.getCannotBeDisabled(), command.command))
                return ErrorCode.CANNOT_BE_DISABLED;

            // Check if the user wanted to add or remove a command
            if (operation.equalsIgnoreCase("add")) {
                // Add the command to the disabled commands list
                ErrorCode error = DisabledCommands.disableCommand(e.getGuild(), command);

                // Make sure the operation succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create embed to tell the user the command was added
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully added " + command.command + " to the list.");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if (operation.equalsIgnoreCase("remove")) {
                // Add the command to the disabled commands list
                ErrorCode error = DisabledCommands.enableCommand(e.getGuild(), command);

                // Make sure the operation succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create embed to tell the user the command was added
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully removed " + command.command + " from the list.");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong args
                return ErrorCode.WRONG_ARGUMENTS;
            }
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
