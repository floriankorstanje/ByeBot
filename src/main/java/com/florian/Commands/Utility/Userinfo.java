package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class Userinfo extends BaseCommand {
    public Userinfo() {
        super.command = "userinfo";
        super.description = "Shows info about you or a user specified.";
        super.arguments = "[user]";
        super.optionalArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Set up the member to get info from. This will get changed if the user entered an argument with a user ID
        Member m = e.getMember();

        // Also get a cool embed
        EmbedBuilder embed = Util.defaultEmbed();

        // Check if the user entered an argument with a user ID to get info about that user
        if(args.length == 1) {
            try {
                // Get the member from the specified user ID
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                // Return unknown UID error
                return ErrorCode.UNKNOWN_ID;
            }
        }

        // Get info about the specified user
        embed.setImage(m.getUser().getAvatarUrl());
        embed.addField("User", m.getAsMention(), true);
        embed.addField("User ID", "`" + m.getId() + "`", true);
        embed.addField("Member Color", String.format("`#%06X`", m.getColor().getRGB() & 0xFFFFFF), true);
        embed.addField("Joined Discord", String.format("%s%n", m.getUser().getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME)), true);
        embed.addField("Joined Guild", String.format("%s%n", m.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME)), true);
        embed.addField("Server Owner", m.isOwner() ? "Yes" : "No", true);

        // Get a list of all the roles the user has
        StringBuilder builder = new StringBuilder();
        for(Role role : m.getRoles())
            builder.append(role.getAsMention()).append(" ");
        embed.addField("Roles", builder.toString(), false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
