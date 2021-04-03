package com.florian.Commands.Score;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.ScoreSystem.ScoreSystem;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ScoreCommand extends BaseCommand {
    public ScoreCommand() {
        super.command = "score";
        super.description = "Shows the amount of score you have.";
        super.arguments = "[user]";
        super.commandType = CommandType.SCORE;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length < 2) {
            Member member = e.getMember();

            // Check if user entered other user to get score from
            if (args.length == 1)
                member = e.getGuild().retrieveMemberById(args[0]).complete();

            // Get score
            int score = ScoreSystem.getScore(e.getGuild(), member.getId());

            // Create embed to send
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Score of " + member.getUser().getAsTag());

            // Fill embed
            embed.addField("Score", String.valueOf(score), false);

            // Send message
            e.getChannel().sendMessage(embed.build()).queue();
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
