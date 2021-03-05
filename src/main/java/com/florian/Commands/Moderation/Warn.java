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

public class Warn extends BaseCommand {
    public Warn() {
        super.command = "warn";
        super.description = "Warns a user.";
        super.arguments = "<user> <reason>";
        super.permission = Permission.KICK_MEMBERS;
        super.moderation = true;
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Command needs at least 2 arguments.
        if(args.length >= 2) {
            // Add all args from index 1 to a string as reason
            StringBuilder reason = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");

                // Reason cannot contain commas since the history file uses commas to separate values
                if(args[i].contains(","))
                    return ErrorCode.UNALLOWED_CHARACTER;
            }

            Member m;
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                // Couldn't get member so return unknown ID
                return ErrorCode.UNKNOWN_ID;
            }

            // Create an embed to send to the warned user
            EmbedBuilder embed = Util.defaultEmbed();

            // Set the embed title
            embed.setTitle("You have been warned in " + e.getGuild().getName());
            embed.setColor(0xFF0000);

            // Fill in the embed
            embed.addField("Warned By", e.getMember().getAsMention(), false);
            embed.addField("Reason", reason.toString(), false);

            // Send the warned user a DM to let them know they've been warned
            try {
                m.getUser().openPrivateChannel().complete().sendMessage(embed.build()).queue();
            } catch (Exception ex) {
                // This isn't a fatal error, so we just tell the person that's warning the other that the warn was successful but the user didn't receive the message
                e.getChannel().sendMessage("I couldn't send a message to **" + m.getUser().getAsTag() + "**, but I've added the warning to their history.").queue();
            }

            // Reuse the old embed to tell the user the warn was successful
            embed = Util.defaultEmbed();

            // Set the title
            embed.setTitle(m.getUser().getAsTag() + " was warned");

            // Fill in the embed
            embed.addField("Warned By", e.getMember().getAsMention(), false);
            embed.addField("Reason", reason.toString(), false);

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();

            // Add this to the user's history if everything succeeded
            UserHistory.addEntry(e.getGuild(), m, e.getMember(), OffenseType.WARN, reason.toString());
        } else {
            // There aren't enough arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
