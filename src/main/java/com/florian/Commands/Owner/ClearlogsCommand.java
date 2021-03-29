package com.florian.Commands.Owner;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ClearlogsCommand extends BaseCommand {
    public ClearlogsCommand() {
        super.command = "clearlogs";
        super.description = "Clears all the logs the bot created.";
        super.commandType = CommandType.OWNER;
        super.aliases.add("deletelogs");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Clear the logs
        ErrorCode error = Log.clearLogs();

        // Check if error is SUCCESS, if not, tell the user
        if (error != ErrorCode.SUCCESS)
            return error;

        // Create embed to tell the user the log was cleared
        EmbedBuilder embed = Util.defaultEmbed();

        // Set title
        embed.setTitle("Cleared logs");

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // return success
        return ErrorCode.SUCCESS;
    }
}
