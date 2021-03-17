package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.UserHistory.UserHistory;
import com.florian.UserHistory.UserHistoryEntry;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.util.Date;

public class HistoryCommand extends BaseCommand {
    public HistoryCommand() {
        super.command = "history";
        super.description = "Shows, or edits someones history.";
        super.arguments = "<user> [operation(edit/remove)] [history-id] [new-reason]";
        super.permission = Permission.KICK_MEMBERS;
        super.userType = UserType.MODERATOR;
        super.requiredArguments = true;
        super.examples.add("399594813390848002");
        super.examples.add("399594813390848002 edit 178218de667 This is a new reason");
        super.examples.add("399594813390848002 remove 178218de667");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Save user id
            String user = args[0];

            // Get all the entries
            Pair<UserHistoryEntry[], ErrorCode> entries = UserHistory.getAllHistory(e.getGuild(), user);

            // If entries failed, return the error
            if (entries.getRight() != ErrorCode.SUCCESS)
                return entries.getRight();

            // Create embed to show all entries
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("History for `" + user + "`");

            // Fill embed
            for (int i = 0; i < entries.getLeft().length; i++) {
                // Get entry as easy variable
                UserHistoryEntry entry = entries.getLeft()[i];

                // Save the executors ID so we can try and change it to a tag
                String executor = entry.getExecutor();

                // Try to change the executors ID to a tag if the user is still in the guild
                try {
                    executor = e.getGuild().retrieveMemberById(executor).complete().getUser().getAsTag();
                } catch (Exception ignored) {
                }

                // Add it to the embed
                embed.addField("Entry #" + i, "ID: `" + entry.getId() + "`\nIssued By: `" + executor + "`\nDate Issued: " + Util.formatDateAgo(new Date(entry.getTime())) + "\nType: `" + entry.getType() + "`\nReason: " + entry.getReason(), false);
            }

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length >= 2) {
            String operation = args[1];

            // Save user id
            String user = args[0];

            if (operation.equalsIgnoreCase("edit")) {
                // Get entry ID
                String id = args[2];

                StringBuilder newReason = new StringBuilder();
                for (int i = 3; i < args.length; i++) {
                    newReason.append(args[i]).append(" ");

                    if (args[i].contains(","))
                        return ErrorCode.UNALLOWED_CHARACTER;
                }

                ErrorCode error = UserHistory.editEntry(e.getGuild(), user, id, newReason.toString());
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create an embed to let the user know the history updated
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Updated history for `" + user + "`");

                // Fill in embed
                embed.addField("Changed By", e.getMember().getAsMention(), false);
                embed.addField("History ID", "`" + id + "`", false);
                embed.addField("New Reason", newReason.toString(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if (operation.equalsIgnoreCase("remove")) {
                // Get entry ID
                String id = args[2];

                ErrorCode error = UserHistory.removeEntry(e.getGuild(), user, id);
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create an embed to let the user know the history was removed
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Removed history for `" + user + "`");

                // Fill in embed
                embed.addField("Removed By", e.getMember().getAsMention(), false);
                embed.addField("History ID", "`" + id + "`", false);

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
