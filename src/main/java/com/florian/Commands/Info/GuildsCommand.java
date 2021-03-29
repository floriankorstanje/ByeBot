package com.florian.Commands.Info;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.Config.GuildConfig;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildsCommand extends BaseCommand {
    public GuildsCommand() {
        super.command = "guilds";
        super.description = "Shows a list of guilds this bot is in.";
        super.aliases.add("guidlist");
        super.commandType = CommandType.INFO;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create an embed because, who doesn't love them?
        EmbedBuilder embed = Util.defaultEmbed();

        // Set embed title
        embed.setTitle(Vars.appInfo.getName() + " guild list - Total: " + e.getJDA().getGuilds().size());

        // Make sure the current guild is always added
        addGuildToEmbed(embed, e.getGuild());

        // Keep track of the amount of embeds added so there aren't more than 15
        int added = 1;

        // Get all the guilds the bot is in and add them to the embed
        for (Guild g : e.getJDA().getGuilds()) {
            if (added < Vars.maxGuildsInEmbed) {
                // Make sure the current guild doesn't get re-added
                if (!g.getId().equalsIgnoreCase(e.getGuild().getId()))
                    addGuildToEmbed(embed, g);
                added++;
            } else
                break;
        }

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }

    private void addGuildToEmbed(EmbedBuilder embed, Guild g) {
        // This is a helper function to add a guild to the list. Just so I don't have to repeat the same line of code
        embed.addField(g.getName(), "Members: `" + g.getMemberCount() + "`\nOwner: `" + g.retrieveOwner().complete().getUser().getAsTag() + "`\nID: `" + g.getId() + "`\nCommands Executed: `" + GuildConfig.getCommandCounter(g) + "`", true);
    }
}
