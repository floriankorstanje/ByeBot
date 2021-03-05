package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Modhelp extends BaseCommand {
    public Modhelp() {
        super.command = "modhelp";
        super.description = "Lists all the moderation commands.";
        super.aliases.add("adminhelp");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Make an embed because they look good
        EmbedBuilder embed = Util.defaultEmbed();

        // Get instance of Help for command and args
        Help help = new Help();

        // Set some basic info for the embed
        embed.setTitle("Moderation help for " + e.getJDA().getSelfUser().getName() + " version " + Vars.version);
        embed.addField("You can type `" + Vars.botPrefix + help.command + " " + help.arguments + "` to get more specific help about a command.", "", false);
        embed.addField("To view other commands, type `" + Vars.botPrefix + help.command + "`", "", false);

        // Add all the commands and their descriptions to the list
        for (BaseCommand command : Vars.commands) {
            // Only add commands that need permission to execute (mod commands)
            if (command.moderation)
                embed.addField(Vars.botPrefix + command.command, command.description, false);
        }

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
