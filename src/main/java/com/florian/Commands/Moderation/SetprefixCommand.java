package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.GuildConfig.GuildConfig;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SetprefixCommand extends BaseCommand {
    public SetprefixCommand() {
        super.command = "setprefix";
        super.description = "Sets the prefix of the bot for this guild.";
        super.arguments = "<prefix>";
        super.requiredArguments = true;
        super.examples.add("?");
        super.examples.add("ByeBot!");
        super.userType = UserType.MODERATOR;
        super.permission = Permission.MANAGE_SERVER;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Save old prefix
            String oldPrefix = GuildConfig.getPrefix(e.getGuild());

            // Set new prefix
            GuildConfig.setPrefix(e.getGuild(), args[0]);

            // Tell the user the prefix changed
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Prefix changed");

            // Fill embed
            embed.addField("Old Prefix", "`" + oldPrefix + "`", true);
            embed.addField("New Prefix", "`" + args[0] + "`", true);

            // Send embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
