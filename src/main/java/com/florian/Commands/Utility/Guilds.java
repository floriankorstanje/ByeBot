package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Guilds extends BaseCommand {
    public Guilds() {
        super.command = "guilds";
        super.description = "Shows a list of guilds this bot is in.";
        super.aliases.add("guidlist");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create an embed because, who doesn't love them?
        EmbedBuilder embed = Util.defaultEmbed();

        // Set embed title
        embed.setTitle(Vars.appInfo.getName() + " guild list");

        // Get all the guilds the bot is in and add them to the embed
        for (Guild g : e.getJDA().getGuilds())
            embed.addField(g.getName(), "Members: `" + g.getMemberCount() + "`\nOwner: `" + g.retrieveOwner().complete().getUser().getAsTag() + "`\nID: `" + g.getId() + "`", true);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
