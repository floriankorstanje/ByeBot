package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DisabledcommandsCommand extends BaseCommand {
    public DisabledcommandsCommand() {
        super.command = "disabledcommands";
        super.description = "Enables or disables a command.";
        super.arguments = "[add/remove] [command]";
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.MANAGE_PERMISSIONS;
        super.examples.add("");
        super.examples.add("add hostinfo");
        super.examples.add("remove avatar");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        return super.execute(e, args);
    }
}
