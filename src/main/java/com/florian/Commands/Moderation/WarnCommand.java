package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.UserHistory.OffenseType;
import com.florian.UserHistory.UserHistory;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WarnCommand extends BaseCommand {
    public WarnCommand() {
        super.command = "warn";
        super.description = "Warns a user.";
        super.arguments = "<user> <reason>";
        super.permission = Permission.KICK_MEMBERS;
        super.commandType = CommandType.MODERATION;
        super.requiredArguments = true;
        super.examples.add("399594813390848002 Acting sus");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Command needs at least 2 arguments.
        if (args.length >= 2) {
            // Add all args from index 1 to a string as reason
            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                reason.append(args[i]).append(" ");

            Member m;
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                // Couldn't get member so return unknown ID
                return ErrorCode.UNKNOWN_ID;
            }

            // Add this to the user's history if everything succeeded
            ErrorCode error = UserHistory.addEntry(e.getGuild(), m.getId(), e.getMember(), OffenseType.WARN, Util.generateId(), reason.toString());

            // If addEntry failed, return the error
            if (error != ErrorCode.SUCCESS)
                return error;

            // Create an embed to send to the warned user
            EmbedBuilder embed = Util.defaultEmbed();

            // Set the title
            embed.setTitle(m.getUser().getAsTag() + " was warned");

            // Fill in the embed
            embed.addField("Warned By", e.getMember().getAsMention(), false);
            embed.addField("Reason", reason.toString(), false);

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else {
            // There aren't enough arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
