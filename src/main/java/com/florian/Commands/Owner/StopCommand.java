package com.florian.Commands.Owner;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand extends BaseCommand {
    public StopCommand() {
        super.command = "stop";
        super.description = "Stops the bot.";
        super.commandType = CommandType.OWNER;
        super.aliases.add("shutdown");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Say bye
        e.getChannel().sendMessage("Shutting down. I was up for `" + Util.getUptime() + "`").queue();

        // Stop the bot
        System.exit(0);

        // Return success
        return ErrorCode.SUCCESS;
    }
}
