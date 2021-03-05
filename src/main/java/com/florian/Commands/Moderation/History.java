package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.UserHistory.UserHistory;
import com.florian.UserHistory.UserHistoryEntries;
import com.florian.UserHistory.UserHistoryEntry;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

public class History extends BaseCommand {
    public History() {
        super.command = "history";
        super.description = "Shows, or edits someones history.";
        super.arguments = "<user> [get|edit|remove] [history-id] [new-reason]";
        super.permission = Permission.KICK_MEMBERS;
        super.moderation = true;
        super.requiredArguments = true;
        super.aliases.add("h");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length >= 2) {
            String operation = args[1];

            // Get the member before doing something else so we don't have to repeat this
            Member m;
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                // Failed to get member
                return ErrorCode.UNKNOWN_ID;
            }

            if (operation.equalsIgnoreCase("get")) {
                // Get all the entries
                UserHistoryEntries entries = UserHistory.getHistory(e.getGuild(), m);

                // Create embed to show all entries
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("History for " + m.getUser().getAsTag());

                // Fill embed
                for (UserHistoryEntry entry : entries.getEntries()) {
                    // Save the executors ID so we can try and change it to a tag
                    String executor = entry.getExecutor();

                    // Try to change the executors ID to a tag if the user is still in the server
                    try {
                        executor = e.getGuild().retrieveMemberById(executor).complete().getUser().getAsTag();
                    } catch (Exception ignored) {
                    }

                    // Add it to the embed
                    embed.addField("Entry #" + entry.getId(), "Issued By: `" + executor + "`\nDate Issued: `" + Util.getTimeAgo(new Date(entry.getTime())) + "`\nType: `" + entry.getOffense() + "`\nReason: " + entry.getReason(), false);
                }

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if (operation.equalsIgnoreCase("edit")) {
                int entry;
                try {
                    entry = Integer.parseInt(args[2]);
                } catch (Exception ex) {
                    // Couldn't parse to int
                    return ErrorCode.WRONG_ARGUMENTS;
                }

                StringBuilder newReason = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    newReason.append(args[i]).append(" ");

                    if (args[i].contains(","))
                        return ErrorCode.UNALLOWED_CHARACTER;
                }

                ErrorCode error = UserHistory.editEntry(e.getGuild(), m, entry, newReason.toString());
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create an embed to let the user know the history updated
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Updated history for " + m.getUser().getAsTag());

                // Fill in embed
                embed.addField("Changed By", e.getMember().getAsMention(), false);
                embed.addField("New Reason", newReason.toString(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if (operation.equalsIgnoreCase("remove")) {
                int entry;
                try {
                    entry = Integer.parseInt(args[2]);
                } catch (Exception ex) {
                    // Couldn't parse to int
                    return ErrorCode.WRONG_ARGUMENTS;
                }

                ErrorCode error = UserHistory.removeEntry(e.getGuild(), m, entry);
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create an embed to let the user know the history was removed
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Removed history for " + m.getUser().getAsTag());

                // Fill in embed
                embed.addField("Removed By", e.getMember().getAsMention(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong arguments
                return ErrorCode.UNKNOWN_OPERATION;
            }
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
