package com.florian.Commands.Help;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Help extends BaseCommand {
    public Help() {
        super.command = "help";
        super.description = "Shows all the commands and how to use them.";
        super.arguments = "[command]";
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Make an embed because they look good
        EmbedBuilder embed = Util.defaultEmbed();

        // Check if the user wants more advanced info about a command by specifying it as an argument
        if (args.length == 1) {
            for (BaseCommand command : Vars.commands) {
                if (command.command.equalsIgnoreCase(args[0]) || Util.containsIgnoreCase(command.aliases, args[0])) {
                    // Set title
                    embed.setTitle("Command info for " + Vars.botPrefix + command.command);

                    // Add all the info about the command
                    embed.addField("Name", command.command, false);
                    embed.addField("Description", command.description, false);
                    embed.addField("Usage", "`" + Vars.botPrefix + command.command + (command.requiredArguments ? " " + command.arguments + "`" : "`"), false);
                    embed.addField("Aliases", command.aliases.size() == 0 ? "none" : "`" + String.join("` `", command.aliases) + "`", false);
                    embed.addField("Permission", command.permission == null ? "none" : "`" + command.permission.toString() + "`", false);

                    // Send the embed to the user
                    e.getChannel().sendMessage(embed.build()).queue();

                    // Return so it doesn't send the entire command list again
                    return ErrorCode.SUCCESS;
                }
            }

            // Command wasn't recognized. Tell the user
            return ErrorCode.WRONG_ARGUMENTS;
        } else if (args.length > 1) {
            // There's too many arguments, so return an error
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Set some basic info for the embed
        embed.setTitle("Help for " + e.getJDA().getSelfUser().getName() + " version " + Vars.version);
        embed.addField("You can type `" + Vars.botPrefix + this.command + " " + this.arguments + "` to get more specific help about a command.", "", false);

        // Add all the commands and their descriptions to the list
        for (BaseCommand command : Vars.commands) {
            // Only add commands that don't need permission to execute (mod commands)
            if (command.userType == UserType.EVERYONE)
                embed.addField(Vars.botPrefix + command.command, command.description, false);
        }

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
