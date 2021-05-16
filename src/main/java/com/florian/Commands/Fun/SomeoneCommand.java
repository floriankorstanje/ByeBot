package com.florian.Commands.Fun;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class SomeoneCommand extends BaseCommand {
    public SomeoneCommand() {
        super.command = "someone";
        super.description = "Pings a random user.";
        super.commandType = CommandType.FUN;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Get random member
        List<Member> members = e.getGuild().getMembers();
        Member member = members.get(Vars.random.nextInt(members.size()));

        // Ping member
        e.getChannel().sendMessage(member.getAsMention()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
