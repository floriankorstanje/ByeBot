package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.UserHistory.OffenseType;
import com.florian.UserHistory.UserHistory;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class History extends BaseCommand {
    public History() {
        super.command = "history";
        super.description = "Shows, or edits someones history.";
        super.arguments = "<user> [get|edit|remove] [history-id] [new-reason]";
        super.requiredArguments = true;
        super.optionalArguments = true;
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
                // Get location for file
                String folder = Util.getGuildFolder(e.getGuild()) + Vars.historyFolder;
                String file = folder + m.getId();

                // Check if folder exists
                File historyFolder = new File(folder);
                if (!historyFolder.exists()) {
                    boolean success = historyFolder.mkdirs();

                    // If it couldn't create the folder, quit
                    if (!success) {
                        System.out.println("Couldn't create history folder for guild " + e.getGuild().getId() + " (" + e.getGuild().getName() + ")");
                        return ErrorCode.OTHER_ERROR;
                    }

                    // If it did create, return no history because there is no history files for this server
                    return ErrorCode.NO_USER_HISTORY;
                }

                // If the file doesn't exist, there is also no history
                if (!new File(file).exists())
                    return ErrorCode.NO_USER_HISTORY;

                // Get all lines in the file
                List<String> lines;
                try {
                    lines = Util.readSmallTextFile(file);
                } catch (IOException ex) {
                    System.out.println("Couldn't read user history for user " + m.getId() + "(" + m.getUser().getAsTag() + ") in server " + e.getGuild().getId() + " (" + e.getGuild().getName() + ")");
                    return ErrorCode.OTHER_ERROR;
                }

                // Make sure lines isn't null
                if (lines == null)
                    return ErrorCode.OTHER_ERROR;

                // Check if there is any history
                if(lines.size() == 0)
                    return ErrorCode.NO_USER_HISTORY;

                // Formatter for the date
                DateFormat formatter = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy z");

                // Create embed to show all entries
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("History for " + m.getUser().getAsTag());

                // Fill embed
                // Entries are formatted as following: executor-id,epoch-time,offense,entry
                for (int i = 0; i < lines.size(); i++) {
                    String[] data = lines.get(i).split(",");
                    String executor = data[0];
                    Date date = new Date(Long.parseLong(data[1]));
                    OffenseType type = OffenseType.valueOf(data[2]);
                    String reason = data[3];

                    // Try to change the executors ID to a tag if the user is still in the server
                    try {
                        executor = e.getGuild().retrieveMemberById(executor).complete().getUser().getAsTag();
                    } catch (Exception ignored) {
                    }

                    // Add it to the embed
                    embed.addField("Entry #" + i, "Issued By: `" + executor + "`\nDate Issued: `" + formatter.format(date) + "`\nType: `" + type.toString() + "`\nReason: " + reason, false);
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
