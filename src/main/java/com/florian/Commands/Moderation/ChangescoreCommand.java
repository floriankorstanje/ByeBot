package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.ScoreSystem.ScoreSystem;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ChangescoreCommand extends BaseCommand {
    public ChangescoreCommand() {
        super.command = "changescore";
        super.description = "Changes a user's score.";
        super.aliases.add("modscore");
        super.aliases.add("editscore");
        super.arguments = "<user> <operation(add/remove/set/clear)> [score]";
        super.requiredArguments = true;
        super.examples.add("399594813390848002 add 100");
        super.examples.add("399594813390848002 remove 25");
        super.examples.add("399594813390848002 set 2048");
        super.examples.add("399594813390848002 clear");
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.KICK_MEMBERS;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 2) {
            // Make sure user was actually trying to clear score
            if (args[1].equalsIgnoreCase("clear")) {
                // Check if user is valid
                if (!Util.validUser(e.getGuild(), args[0]))
                    return ErrorCode.UNKNOWN_ID;

                // Clear score
                ErrorCode error = ScoreSystem.setScore(e.getGuild(), args[0], 0);

                // Make sure setScore succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                sendEmbed(e.getGuild(), e.getChannel(), args[0], 0);
            }
        } else if (args.length == 3) {
            // Save score to set
            int score = 0;
            try {
                score = Integer.parseInt(args[2]);
            } catch (Exception ignored) {
                return ErrorCode.INVALID_SCORE;
            }

            // Check what the user wanted to do
            if (args[1].equalsIgnoreCase("add")) {
                // Check if user is valid
                if (!Util.validUser(e.getGuild(), args[0]))
                    return ErrorCode.UNKNOWN_ID;

                int newScore = ScoreSystem.getScore(e.getGuild(), args[0]) + score;

                // Add score
                ErrorCode error = ScoreSystem.setScore(e.getGuild(), args[0], newScore);

                // Make sure setScore succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                sendEmbed(e.getGuild(), e.getChannel(), args[0], newScore);
            } else if (args[1].equalsIgnoreCase("remove")) {
                // Check if user is valid
                if (!Util.validUser(e.getGuild(), args[0]))
                    return ErrorCode.UNKNOWN_ID;

                int newScore = ScoreSystem.getScore(e.getGuild(), args[0]) - score;

                // Add score
                ErrorCode error = ScoreSystem.setScore(e.getGuild(), args[0], newScore);

                // Make sure setScore succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                sendEmbed(e.getGuild(), e.getChannel(), args[0], newScore);
            } else if (args[1].equalsIgnoreCase("set")) {
                // Check if user is valid
                if (!Util.validUser(e.getGuild(), args[0]))
                    return ErrorCode.UNKNOWN_ID;

                // Set score
                ErrorCode error = ScoreSystem.setScore(e.getGuild(), args[0], score);

                // Make sure setScore succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                sendEmbed(e.getGuild(), e.getChannel(), args[0], score);
            }
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    private void sendEmbed(Guild g, TextChannel channel, String user, int score) {
        String userMention = "`[" + user + "]`";

        // Get user as mention or as ID
        if (Util.validUser(g, user))
            userMention = g.retrieveMemberById(user).complete().getAsMention();

        // Create embed to tell user score was modified
        EmbedBuilder embed = Util.defaultEmbed();

        // Set title
        embed.setTitle("Changed score");

        // Fill embed
        embed.addField("User", userMention, false);
        embed.addField("New Score", "`" + score + "`", false);

        // Send message
        channel.sendMessage(embed.build()).queue();
    }
}
