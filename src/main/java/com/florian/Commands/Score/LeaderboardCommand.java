package com.florian.Commands.Score;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.ScoreSystem.ScoreSystem;
import com.florian.ScoreSystem.UserScore;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

public class LeaderboardCommand extends BaseCommand {
    public LeaderboardCommand() {
        super.command = "leaderboard";
        super.description = "Shows the users with the highest score.";
        super.aliases.add("lb");
        super.commandType = CommandType.SCORE;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Get the leaderboard for this guild
        Pair<UserScore[], ErrorCode> leaderboard = ScoreSystem.getLeaderboard(e.getGuild(), Vars.maxLeaderboardPlaces);

        // Make sure ErrorCode is success
        if(leaderboard.getRight() != ErrorCode.SUCCESS)
            return leaderboard.getRight();

        // Get amount of places
        int places = leaderboard.getLeft().length;

        // Create embed to show leaderboard
        EmbedBuilder embed = Util.defaultEmbed();

        // Set title
        embed.setTitle("Leaderboard for " + e.getGuild().getName());

        // Add all the places
        for (int i = places; i > 0; i--) {
            // Get leaderboard element
            UserScore elem = leaderboard.getLeft()[i - 1];

            // Get the user's ID
            String user = "`[" + elem.getUser() + "]`";

            // Try and get the user's actual name if they're still in the guild
            try {
                user = e.getGuild().retrieveMemberById(elem.getUser()).complete().getEffectiveName();
            } catch (Exception ex) {
                // Couldn't get user's name so just stay with the ID
            }

            // Add to the embed
            embed.addField("#" + (places - i + 1) + ": " + user, "Score: `" + elem.getScore() + "`", false);
        }

        // Send embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
