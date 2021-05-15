package com.florian.Commands.Owner;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetstatusCommand extends BaseCommand {
    public SetstatusCommand() {
        super.command = "setstatus";
        super.description = "Sets the bots status.";
        super.commandType = CommandType.OWNER;
        super.arguments = "<status|clear>";
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length >= 1) {
            // Check what the user wanted to do
            if (args[0].equalsIgnoreCase("clear") && args.length == 1) {
                // Clear the status
                Vars.customStatus = "";

                // Create embed to tell user
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully cleared status!");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Set the status to the arguments
                StringBuilder status = new StringBuilder();
                for (String arg : args)
                    status.append(arg).append(" ");
                Vars.customStatus = status.toString();

                // Create embed to tell user
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully set status!");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            }
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
