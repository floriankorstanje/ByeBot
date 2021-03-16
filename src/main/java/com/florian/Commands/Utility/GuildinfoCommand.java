package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

public class GuildinfoCommand extends BaseCommand {
    public GuildinfoCommand() {
        super.command = "guildinfo";
        super.description = "Gives some info about the guild.";
        super.aliases.add("guild");
        super.aliases.add("server");
        super.aliases.add("serverinfo");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create a cool embed because we all love them
        EmbedBuilder embed = Util.defaultEmbed();

        // Set embed title and image
        embed.setTitle(e.getGuild().getName() + " info");
        embed.setImage(e.getGuild().getIconUrl());

        // Fill in the embed with guild info
        embed.addField("Guild ID", "`" + e.getGuild().getId() + "`", true);
        embed.addField("Guild Region", "`" + e.getGuild().getRegionRaw() + "`", true);
        embed.addField("Owner", e.getGuild().retrieveOwner().complete().getAsMention(), true);
        embed.addField("Boosts", "`" + e.getGuild().getBoostCount() + "/30` (Tier " + e.getGuild().getBoostTier().getKey() + ")", true);
        embed.addField("Roles", "`" + e.getGuild().getRoles().size() + "`", true);
        embed.addField("Members", "`" + e.getGuild().getMemberCount() + "`", true);
        embed.addField("Channels", "Total: `" + e.getGuild().getChannels().size() + "`\nVoice: `" + e.getGuild().getVoiceChannels().size() + "`\nText: `" + e.getGuild().getTextChannels().size() + "`", true);
        embed.addField("Time Created", Util.formatDateAgo(new Date(e.getGuild().getTimeCreated().toEpochSecond() * 1000)), true);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
