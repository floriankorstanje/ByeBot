package com.florian.Commands.Fun;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RollCommand extends BaseCommand {
    public RollCommand() {
        super.command = "roll";
        super.description = "Generates a random number. Max must be greater than 0 and smaller than " + Integer.MAX_VALUE + ".";
        super.arguments = "[max]";
        super.examples.add("100");
        super.aliases.add("random");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if(args.length > 1) {
            // Too many arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Max number
        int max = 100;

        // Set max if it was specified
        if(args.length == 1) {
            try {
                max = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                return ErrorCode.WRONG_ARGUMENTS;
            }
        }

        if(max < 1)
            return ErrorCode.WRONG_ARGUMENTS;

        // Generate number. Add one so $roll 100 can return 100
        int rnd = Vars.random.nextInt(max + 1);

        // Create embed to send
        EmbedBuilder embed = Util.defaultEmbed(false);

        // Fill embed
        embed.setTitle(String.valueOf(rnd));

        // Send embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
