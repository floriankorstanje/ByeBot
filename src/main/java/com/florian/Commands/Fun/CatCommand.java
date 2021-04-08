package com.florian.Commands.Fun;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CatCommand extends BaseCommand {
    public CatCommand() {
        super.command = "cat";
        super.description = "Sends my favorite cat image!";
        super.advancedDescription = "I want to change this to a random cat image in the future, but I haven't found a nice API yet :(";
        super.commandType = CommandType.FUN;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Send the cat
        String catUrl = "https://tenor.com/view/cat-kitty-cope-gif-20110606";
        e.getChannel().sendMessage(catUrl).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
