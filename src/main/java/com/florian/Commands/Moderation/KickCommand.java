package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.UserHistory.OffenseType;
import com.florian.UserHistory.UserHistory;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class KickCommand extends BaseCommand {
    public KickCommand() {
        super.command = "kick";
        super.description = "Kicks a user.";
        super.arguments = "<user> [reason]";
        super.permission = Permission.KICK_MEMBERS;
        super.userType = UserType.MODERATOR;
        super.requiredArguments = true;
        super.examples.add("399594813390848002 You are now kicked");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length < 1)
            return ErrorCode.WRONG_ARGUMENTS;

        // Add all args from index 1 to a string as reason
        StringBuilder reason = new StringBuilder();

        // If there is more than 1 argument, there is also a reason. So we can append the reason to the stringbuilder
        if (args.length >= 2) {
            for (int i = 1; i < args.length; i++)
                reason.append(args[i]).append(" ");
        } else {
            // If there is no reason provided, put it to none
            reason.append("none");
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

        // Add this to the user's history if everything succeeded
        ErrorCode error = UserHistory.addEntry(e.getGuild(), m.getId(), e.getMember(), OffenseType.KICK, Util.generateId(), reason.toString());

        // If addEntry didn't succeed, return the error
        if(error != ErrorCode.SUCCESS)
            return error;

        // Create an embed to tell the user the kick was successful
        EmbedBuilder embed = Util.defaultEmbed();

        // Set the title
        embed.setTitle(m.getUser().getAsTag() + " was kicked");

        // Fill in the embed
        embed.addField("Kicked By", e.getMember().getAsMention(), false);
        embed.addField("Reason", reason.toString(), false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
