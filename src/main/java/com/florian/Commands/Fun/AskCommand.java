package com.florian.Commands.Fun;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AskCommand extends BaseCommand {
    public AskCommand() {
        super.command = "ask";
        super.description = "You can ask a yes/no question and the bot will give an 8-ball-like answer.";
        super.arguments = "<question>";
        super.requiredArguments = true;
        super.commandType = CommandType.FUN;
        super.examples.add("Is ByeBot cool?");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length > 0) {
            // Get the question
            StringBuilder question = new StringBuilder();
            for (String arg : args)
                question.append(arg).append(" ");

            // All the answers
            String[] answers = {"Yes", "No", "Definitely not", "Most likely", "Maybe", "Definitely"};

            // Get random answer
            String answer = answers[Vars.random.nextInt(answers.length)];

            // Create embed
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Answer for " + e.getMember().getUser().getAsTag());

            // Fill embed
            embed.addField("Question asked", question.toString(), false);
            embed.addField("Answer", answer, false);

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
