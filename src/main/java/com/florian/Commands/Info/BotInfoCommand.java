package com.florian.Commands.Info;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.Commands.Utility.HelpCommand;
import com.florian.Config.BotConfig;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BotInfoCommand extends BaseCommand {
    public BotInfoCommand() {
        super.command = "botinfo";
        super.description = "Provides some information about the bot.";
        super.aliases.add("bot");
        super.commandType = CommandType.INFO;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create an embed because normal messages are ugly
        EmbedBuilder embed = Util.defaultEmbed();

        // Get bot invite URL
        String url = Vars.appInfo.getInviteUrl(Permission.ADMINISTRATOR);

        // Set some embed info
        embed.setTitle(Vars.appInfo.getName() + " info");

        // Fill the embed
        embed.addField("Gateway Latency", "`" + e.getJDA().getGatewayPing() + "ms`", false);
        embed.addField("REST Latency", "`" + e.getJDA().getRestPing().complete() + "ms`", false);
        embed.addField("Commands Executed", "`" + BotConfig.getCommandCounter() + "`", false);
        embed.addField("Commands", "Total: `" + Vars.commands.length + "`\n*Type " + Vars.botPrefix + new HelpCommand().command + " to see all the commands.*", false);
        embed.addField("Invite Link", "[Click Here](" + url + ") to add the bot to your guild.", false);
        embed.addField("Bot Profile Picture", "Huge thanks to davour#0917 for making the bot's profile picture.\n[Davour's YouTube](https://www.youtube.com/channel/UCpGriMmtbKl52iMbr6WqZmw)", false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
