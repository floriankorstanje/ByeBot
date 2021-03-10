package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.UserHistory.UserHistory;
import com.florian.UserHistory.UserHistoryEntries;
import com.florian.UserHistory.UserHistoryEntry;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

public class History extends BaseCommand {
    public History() {
        super.command = "history";
        super.description = "Shows, or edits someones history.";
        super.arguments = "<user> [edit|remove] [history-id] [new-reason]";
        super.permission = Permission.KICK_MEMBERS;
        super.userType = UserType.MODERATOR;
        super.requiredArguments = true;
        super.aliases.add("h");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Save user id
            String user = args[0];

            // Get all the entries
            UserHistoryEntries entries = UserHistory.getAllHistory(e.getGuild(), user);

            // If entries failed, return the error
            if (entries.getError() != ErrorCode.SUCCESS)
                return entries.getError();

            // Create embed to show all entries
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("History for `" + user + "`");

            // Fill embed
            for (int i = 0; i < entries.getEntries().length; i++) {
                // Get entry as easy variable
                UserHistoryEntry entry = entries.getEntries()[i];

                // Save the executors ID so we can try and change it to a tag
                String executor = entry.getExecutor();

                // Try to change the executors ID to a tag if the user is still in the guild
                try {
                    executor = e.getGuild().retrieveMemberById(executor).complete().getUser().getAsTag();
                } catch (Exception ignored) {
                }

                // Add it to the embed
                embed.addField("Entry #" + i, "ID: `" + entry.getId() + "`\nIssued By: `" + executor + "`\nDate Issued: " + Util.formatDate(new Date(entry.getTime())) + "\nType: `" + entry.getOffense() + "`\nReason: " + entry.getReason(), false);
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
