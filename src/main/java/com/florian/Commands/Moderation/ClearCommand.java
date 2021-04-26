package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Vars;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ClearCommand extends BaseCommand {
    public ClearCommand() {
        super.command = "clear";
        super.description = "Deletes specified amount of messages from channel.";
        super.advancedDescription = "This can only delete a maximum of 100 messages.";
        super.arguments = "<amount>";
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.MESSAGE_MANAGE;
        super.requiredArguments = true;
        super.examples.add("10");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            int amount;
            try {
                amount = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                // Couldn't parse to int
                return ErrorCode.WRONG_ARGUMENTS;
            }

            // Make sure the user isn't trying to delete more than 100 messages
            if (amount > 100)
                return ErrorCode.WRONG_ARGUMENTS;

            // Confirmation
            e.getChannel().sendMessage("Are you sure you want to remove " + amount + " messages?").queue(message -> {
                // Add reactions for the user to decide
                message.addReaction("\uD83D\uDC4D").queue();
                message.addReaction("\uD83D\uDC4E").queue();

                new Thread(() -> {
                    // Want a bit for the user to decide
                    try {
                        Thread.sleep(Vars.waitForClearReactionDelay * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    if (message.retrieveReactionUsers("\uD83D\uDC4D").complete().contains(e.getMember().getUser())) {
                        // Get date from 2 weeks ago (Because the bot can't delete messages older than 2 weeks)
                        OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);

                        // Get past <amount> message
                        List<Message> messages = e.getChannel().getHistory().retrievePast(amount).complete();

                        // Remove all the messages older than 2 weeks
                        messages.removeIf(msg -> msg.getTimeCreated().isBefore(twoWeeksAgo));

                        // Delete all remaining messages
                        e.getChannel().deleteMessages(messages).complete();
                    } else {
                        // User didn't respond or didn't agree, so cancel
                        message.editMessage("Clear cancelled.").queue();
                        message.clearReactions().complete();
                    }
                }).start();
            });
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
