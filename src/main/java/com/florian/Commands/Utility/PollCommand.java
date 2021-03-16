package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PollCommand extends BaseCommand {
    public PollCommand() {
        super.command = "poll";
        super.description = "Creates a yes or no poll. Question can't be longer than 256 characters.";
        super.arguments = "<question>";
        super.requiredArguments = true;
        super.examples.add("Is this a good bot?");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Add all arguments into a string
        StringBuilder question = new StringBuilder();
        for (String str : args)
            question.append(str).append(" ");

        // The title of an embed can't be longer than 256 characters because Discord said so
        if (question.length() > 256)
            return ErrorCode.QUESTION_TOO_LONG;

        // Emoji
        String thumbsUp = "\uD83D\uDC4D";
        String thumbsDown = "\uD83D\uDC4E";

        // Embed for the poll
        EmbedBuilder embed = Util.defaultEmbed();

        // Set title
        embed.setTitle(question.toString());

        // Fill embed
        embed.addField("Yes: " + thumbsUp + "\nNo: " + thumbsDown, "*Poll by " + e.getMember().getUser().getAsTag() + "*", false);

        // Send embed
        e.getChannel().sendMessage(embed.build()).queue(msg -> {
            // Add the reactions
            msg.addReaction(thumbsUp).queue();
            msg.addReaction(thumbsDown).queue();

            // Delete the message with the command
            e.getMessage().delete().queue();
        });

        // Return success
        return ErrorCode.SUCCESS;
    }
}
