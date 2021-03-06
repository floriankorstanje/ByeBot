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

public class Ban extends BaseCommand {
    public Ban() {
        super.command = "ban";
        super.description = "Bans a user.";
        super.arguments = "<user> [reason]";
        super.permission = Permission.BAN_MEMBERS;
        super.moderation = true;
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if(args.length < 1)
            return ErrorCode.WRONG_ARGUMENTS;

        // Add all args from index 1 to a string as reason
        StringBuilder reason = new StringBuilder();

        // If there is more than 1 argument, there is also a reason. So we can append the reason to the stringbuilder
        if (args.length >= 2) {
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");

                // Reason cannot contain commas since the history file uses commas to separate values
                if (args[i].contains(","))
                    return ErrorCode.UNALLOWED_CHARACTER;
            }
        } else {
            // If there is no reason provided, put it to none
            reason.append("none");
        }

        // Get the member by ID
        Member m;
        try {
            m = e.getGuild().retrieveMemberById(args[0]).complete();
        } catch (Exception ex) {
            // Couldn't get member so return unknown ID
            return ErrorCode.UNKNOWN_ID;
        }

        // Actually ban the user
        try {
            e.getGuild().ban(m, 1, reason.toString()).complete();
        } catch (HierarchyException ex) {
            // User didn't get banned because bot didn't have enough permissions
            return ErrorCode.NO_PERMISSION;
        } catch (Exception ex) {
            // Something else happened
            return ErrorCode.OTHER_ERROR;
        }

        // Add this to the user's history if everything succeeded
        ErrorCode error = UserHistory.addEntry(e.getGuild(), m, e.getMember(), OffenseType.BAN, reason.toString());

        if (error == ErrorCode.OTHER_ERROR)
            return ErrorCode.OTHER_ERROR;

        if (error != ErrorCode.SUCCESS)
            return error;

        // Create an embed to tell the user the ban was successful
        EmbedBuilder embed = Util.defaultEmbed();

        // Set the title
        embed.setTitle(m.getUser().getAsTag() + " was banned");

        // Fill in the embed
        embed.addField("Banned By", e.getMember().getAsMention(), false);
        embed.addField("Reason", reason.toString(), false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
