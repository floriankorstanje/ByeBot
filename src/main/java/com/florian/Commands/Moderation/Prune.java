package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Vars;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Prune extends BaseCommand {
    public Prune() {
        super.command = "prune";
        super.description = "Deletes specified amount of messages from channel.";
        super.arguments = "<amount>";
        super.moderation = true;
        super.permission = Permission.MESSAGE_MANAGE;
        super.requiredArguments = true;
        super.aliases.add("purge");
        super.aliases.add("clear");
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

            // Confirmation
            e.getChannel().sendMessage("Are you sure you want to remove " + amount + " messages?").queue(message -> {
                // Add reactions for the user to decide
                message.addReaction("\uD83D\uDC4D").queue();
                message.addReaction("\uD83D\uDC4E").queue();

                new Thread(() -> {
                    // Want a bit for the user to decide
                    try {
                        Thread.sleep(Vars.waitForPruneReactionDelay * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    if(message.retrieveReactionUsers("\uD83D\uDC4D").complete().contains(e.getMember().getUser())) {
                        // Start deleting the messages. Add 2 for the users and the bots message
                        clear(e.getChannel(), e.getMember(), amount + 2);
                    } else {
                        // User didn't respond or didn't agree, so cancel
                        message.editMessage("Prune cancelled.").queue();
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

    private void clear(TextChannel channel, Member caller, int amount) {
        // Can't delete earlier than 2 weeks because Discord said so
        OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minus(2, ChronoUnit.WEEKS);

        new Thread(() -> {
            AtomicInteger count = new AtomicInteger();
            AtomicInteger deleted = new AtomicInteger();
            while (true) {
                // Get all messages and remove the ones older than 2 weeks
                List<Message> messages = channel.getHistory().retrievePast(100).complete();
                messages.removeIf(msg -> msg.getTimeCreated().isBefore(twoWeeksAgo));
                messages.removeIf(msg -> count.getAndIncrement() >= amount);

                // If list is empty we're done
                if (messages.isEmpty())
                    break;

                // Actually delete messages
                channel.deleteMessages(messages).complete();

                // Get the count of actually deleted messages
                deleted.addAndGet(messages.size());
            }

            // Tell the user how many message were deleted
            channel.sendMessage(deleted.get() + " messages were deleted by " + caller.getAsMention()).queue();
        }).start();
    }
}
