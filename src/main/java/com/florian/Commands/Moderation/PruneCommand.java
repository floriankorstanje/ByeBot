package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Userlog.Userlog;
import com.florian.Userlog.UserlogEntry;
import com.florian.Vars;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

public class PruneCommand extends BaseCommand {
    public PruneCommand() {
        super.command = "prune";
        super.description = "Kicks all the users that haven't sent a message in the past specified days";
        super.arguments = "<days-inactive>";
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.KICK_MEMBERS;
        super.advancedDescription = "This won't kick users that have never done anything in the server. Only when they joined and did one thing. <days-inactive> must be between 1-365";
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Check if the amount of days given is valid.
            int days;
            try {
                days = Integer.parseInt(args[0]);
            } catch (Exception ignored) {
                return ErrorCode.WRONG_ARGUMENTS;
            }
            if (days < 1 || days > 365)
                return ErrorCode.WRONG_ARGUMENTS;

            // Get list of inactive members
            Pair<UserlogEntry[], ErrorCode> members = Userlog.getInactive(e.getGuild(), days);

            // Send confirmation message
            e.getChannel().sendMessage("Are you sure you want to prune " + members.getLeft().length + " inactive members?").queue(msg -> {
                // Add reactions for the user to decide
                msg.addReaction("\uD83D\uDC4D").queue();
                msg.addReaction("\uD83D\uDC4E").queue();

                new Thread(() -> {
                    // Want a bit for the user to decide
                    try {
                        Thread.sleep(Vars.waitForPruneReactionDelay * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    if (msg.retrieveReactionUsers("\uD83D\uDC4D").complete().contains(e.getMember().getUser())) {
                        for (UserlogEntry entry : members.getLeft()) {
                            try {
                                e.getGuild().kick(entry.getUser(), "Inactive for " + days + " days.").complete();
                            } catch (Exception ignored) {}
                        }

                        // Tell the user members were kicked
                        e.getChannel().sendMessage("Successfully kicked inactive members.").queue();
                    } else {
                        // User didn't respond or didn't agree, so cancel
                        msg.editMessage("Prune cancelled.").queue();
                        msg.clearReactions().complete();
                    }
                }).start();
            });
        } else {
            // Wrong args
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
