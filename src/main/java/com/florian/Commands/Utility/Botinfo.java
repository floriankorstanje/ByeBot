package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Botinfo extends BaseCommand {
    public Botinfo() {
        super.command = "botinfo";
        super.description = "Provides some information about the bot.";
        super.aliases.add("bot");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create an embed because normal messages are ugly
        EmbedBuilder embed = Util.defaultEmbed();

        // Set some embed info
        embed.setTitle(Vars.appInfo.getName() + " info");

        // Fill the embed
        embed.addField("Gateway Latency", "`" + e.getJDA().getGatewayPing() + "ms`", false);
        embed.addField("REST Latency", "`" + e.getJDA().getRestPing().complete() + "ms`", false);
        embed.addField("User ID", "`" + Vars.appInfo.getId() + "`", false);
        embed.addField("Version", "`" + Vars.version + "`", false);
        embed.addField("Commands", "Total: `" + Vars.commands.length + "`\n*Type " + Vars.botPrefix + new Help().command + " to see all the commands.*", false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
