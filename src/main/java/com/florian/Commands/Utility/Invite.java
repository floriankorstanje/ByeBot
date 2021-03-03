package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Invite extends BaseCommand {
    public Invite() {
        this.command = "invite";
        this.description = "Invite link to get this bot in your server.";
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create an embed for the invite
        EmbedBuilder embed = Util.defaultEmbed();

        // Get bot invite URL
        String url = Vars.appInfo.getInviteUrl(Permission.ADMINISTRATOR);

        // Set embed info
        embed.setTitle("Invite for " + Vars.appInfo.getName());
        embed.addField("", "[Click Here](" + url + ") to add the bot to your server.", false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
