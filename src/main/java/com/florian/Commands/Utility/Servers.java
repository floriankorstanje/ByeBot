package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Servers extends BaseCommand {
    public Servers() {
        super.command = "servers";
        super.description = "Shows a list of servers this bot is in.";
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create an embed because, who doesn't love them?
        EmbedBuilder embed = Util.defaultEmbed();

        // Set embed title
        embed.setTitle(Vars.appInfo.getName() + " server list");

        // Get all the servers the bot is in and add them to the embed
        for (Guild g : e.getJDA().getGuilds())
            embed.addField(g.getName(), "Members: `" + g.getMemberCount() + "`\nOwner: `" + g.retrieveOwner().complete().getUser().getAsTag() + "`", true);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
