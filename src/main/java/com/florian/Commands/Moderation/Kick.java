package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.UserHistory.OffenseType;
import com.florian.UserHistory.UserHistory;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class Kick extends BaseCommand {
    public Kick() {
        super.command = "kick";
        super.description = "Kicks a user.";
        super.arguments = "<user> <reason>";
        super.permission = Permission.KICK_MEMBERS;
        super.moderation = true;
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Command needs at least 2 arguments.
        if (args.length >= 2) {
            // Add all args from index 1 to a string as reason
            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");

                // Reason cannot contain commas since the history file uses commas to separate values
                if (args[i].contains(","))
                    return ErrorCode.UNALLOWED_CHARACTER;
            }

            Member m;
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                // Couldn't get member so return unknown ID
                return ErrorCode.UNKNOWN_ID;
            }

            // Actually kick the user
            try {
                e.getGuild().kick(m, reason.toString()).complete();
            } catch (HierarchyException ex) {
                // User didn't get kicked because bot didn't have enough permissions
                return ErrorCode.NO_PERMISSION;
            } catch (Exception ex) {
                // Something else happened
                return ErrorCode.OTHER_ERROR;
            }

            // Create an embed to tell the user the kick was successful
            EmbedBuilder embed = Util.defaultEmbed();

            // Set the title
            embed.setTitle(m.getUser().getAsTag() + " was kicked");

            // Fill in the embed
            embed.addField("Kicked By", e.getMember().getAsMention(), false);
            embed.addField("Reason", reason.toString(), false);

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();

            // Add this to the user's history if everything succeeded
            UserHistory.addEntry(e.getGuild(), m, e.getMember(), OffenseType.KICK, reason.toString());
        } else {
            // There aren't enough arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
