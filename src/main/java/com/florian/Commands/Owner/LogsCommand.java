package com.florian.Commands.Owner;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class LogsCommand extends BaseCommand {
    public LogsCommand() {
        super.command = "logs";
        super.description = "Shows size of logs, deletes them or lists them.";
        super.commandType = CommandType.OWNER;
        super.arguments = "[clear/list]";
        super.aliases.add("log");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 0) {
            // Show log information
            File[] logs = new File(Vars.logsFolder).listFiles();

            // Make sure there is logs
            if (logs == null)
                return ErrorCode.NO_LOGS;

            // Sort logs by date modified
            sortLogs(logs);

            // Get total file size
            long size = 0;
            for (File log : logs) {
                try {
                    size += Files.size(log.toPath());
                } catch (Exception ignored) {
                }
            }

            // Get latest log
            File latestLog = logs[logs.length - 1];

            // Get latest log size
            long latestLogSize = -1;
            try {
                latestLogSize = Files.size(latestLog.toPath());
            } catch (Exception ignored) {
            }


            // Create embed to show information
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle(e.getJDA().getSelfUser().getName() + " log information");

            // Set info
            embed.addField("Logs", "Total Logs: `" + logs.length + "`\nTotal Size: `" + (size / 1024) + "KiB`", false);
            embed.addField("Latest Log", "File Name: `" + latestLog.getName() + "`\nSize: " + (latestLogSize == -1 ? "[Unknown]" : "`" + (latestLogSize / 1024) + "KiB`") + "\nLast Modified: " + Util.formatDateAgo(new Date(latestLog.lastModified())), false);

            // Send
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                // Clear the logs
                ErrorCode error = Log.clearLogs();

                // Check if error is SUCCESS, if not, tell the user
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Create embed to tell the user the log was cleared
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Cleared logs");

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if (args[0].equalsIgnoreCase("list")) {
                // Show all logs
                File[] logs = new File(Vars.logsFolder).listFiles();

                // Make sure there is logs
                if (logs == null)
                    return ErrorCode.NO_LOGS;

                // Sort logs by date modified
                sortLogs(logs);
                Collections.reverse(Arrays.asList(logs));

                // Create embed to list all
                EmbedBuilder embed = Util.defaultEmbed();

                // Set info
                embed.setDescription("Total Logs: " + logs.length);
                embed.setTitle(e.getJDA().getSelfUser().getName() + " log list");

                // Add all log files
                StringBuilder info = new StringBuilder();
                for (File log : logs) {
                    try {
                        StringBuilder temp = new StringBuilder();
                        temp.append("File Name: `").append(log.getName()).append("` - Size: `").append(Files.size(log.toPath()) / 1024).append("KiB`\n");

                        // Make sure embed doesn't exceed 1024 characters
                        if (info.toString().length() + temp.toString().length() > 1024)
                            break;

                        info.append(temp);
                    } catch (IOException ignored) {
                    }
                }

                embed.addField("Logs", info.toString(), false);

                // Send message
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong arguments
                return ErrorCode.WRONG_ARGUMENTS;
            }
        }

        // return success
        return ErrorCode.SUCCESS;
    }

    private void sortLogs(File[] array) {
        File temp;
        for (int i = 1; i < array.length; i++) {
            for (int j = i; j > 0; j--) {
                if (array[j].lastModified() < array[j - 1].lastModified()) {
                    temp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = temp;
                }
            }
        }
    }
}
