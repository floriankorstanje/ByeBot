package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Help extends BaseCommand {
    public Help() {
        super.command = "help";
        super.description = "Shows all the commands and how to use them.";
        super.arguments = "[command]";
        super.optionalArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Make an embed because they look good
        EmbedBuilder embed = Util.defaultEmbed();

        // Check if the user wants more advanced info about a command by specifying it as an argument
        if(args.length == 1) {
            for(BaseCommand command : Vars.commands) {
                if(command.command.equalsIgnoreCase(args[0])) {
                    // Add all the info about the command
                    embed.setTitle("Command info for " + Vars.botPrefix + command.command);
                    embed.addField("Name", command.command, false);
                    embed.addField("Description", command.description, false);
                    embed.addField("Usage", "`" + Vars.botPrefix + command.command + " " + command.arguments + "`", false);
                    embed.addField("Permission", command.permission == null ? "none" : "`" + command.permission.toString() + "`", false);

                    // Send the embed to the user
                    e.getChannel().sendMessage(embed.build()).queue();

                    // Return so it doesn't send the entire command list again
                    return ErrorCode.SUCCESS;
                }
            }

            // Command wasn't recognized. Tell the user
            return ErrorCode.WRONG_ARGUMENTS;
        } else if(args.length > 1) {
            // There's too many arguments, so return an error
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Set some basic info for the embed
        embed.setTitle("Help for " + e.getJDA().getSelfUser().getName() + " version " + Vars.version);
        embed.addField("You can type " + Vars.botPrefix + this.command + " " + this.arguments + " to get more specific help about a command.", "", false);

        // Add all the commands and their descriptions to the list
        for(BaseCommand command : Vars.commands) {
            embed.addField(Vars.botPrefix + command.command, command.description, false);
        }

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
