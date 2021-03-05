package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Userlog.Userlog;
import com.florian.Userlog.UserlogEntries;
import com.florian.Userlog.UserlogEntry;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Lastseen extends BaseCommand {
    public Lastseen() {
        super.command = "lastseen";
        super.description = "Shows when I last saw this user do something in this server. Full gives you the last " + Vars.maxUserlogEntries + " actions of this user.";
        super.arguments = "<user> [full]";
        super.moderation = true;
        super.permission = Permission.KICK_MEMBERS;
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Save member
            Member m;

            // Try to get member from ID
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                return ErrorCode.UNKNOWN_ID;
            }

            // Get all the entries
            UserlogEntries entries = Userlog.getEntries(e.getGuild(), m);

            // If getting entries failed, return
            if (entries.getErrorCode() != ErrorCode.SUCCESS)
                return entries.getErrorCode();

            // Formatter for the date
            DateFormat formatter = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy z");

            // Create an embed
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Lastseen " + m.getUser().getAsTag());

            // Get all entries and add to embed
            for (int i = 0; i < entries.getEntries().length; i++) {
                // Don't add more than 5 entries to the list
                if (i >= 5)
                    break;

                UserlogEntry entry = entries.getEntries()[i];

                // Add entry to the embed
                embed.addField("Entry #" + i, "Time: `" + Util.getTimeAgo(new Date(entry.getTime())) + "`\nAction: `" + entry.getAction() + "`", false);
            }

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length == 2 && args[1].equalsIgnoreCase("full")) {
            // Save member
            Member m;

            // Try to get member from ID
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                return ErrorCode.UNKNOWN_ID;
            }

            // Get all the entries
            UserlogEntries entries = Userlog.getEntries(e.getGuild(), m);

            // If getting entries failed, return
            if (entries.getErrorCode() != ErrorCode.SUCCESS)
                return entries.getErrorCode();

            // Formatter for the date
            DateFormat formatter = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy z");

            // File info
            String tempFilename = Util.getGuildFolder(e.getGuild()) + "temp.txt";
            List<String> fileContents = new ArrayList<>();

            // Add title to file
            fileContents.add("Lastseen full report for " + m.getUser().getAsTag() + "\n\n");

            // Get all entries and add them to a file. Then send the file.
            for (int i = 0; i < entries.getEntries().length; i++) {
                UserlogEntry entry = entries.getEntries()[i];
                fileContents.add(entry.getAction() + "\tat\t" + formatter.format(new Date(entry.getTime())));
            }

            // Write to file
            try {
                Util.writeSmallTextFile(tempFilename, fileContents);
            } catch (IOException ioException) {
                return ErrorCode.OTHER_ERROR;
            }

            // Send the file and tell the user the file will be deleted soon
            AtomicReference<Message> botBessage = new AtomicReference<>();
            e.getChannel().sendMessage("Here's your report. This report will be deleted in " + Vars.deleteUserlogFullDelay + " seconds.").queue(botBessage::set);
            e.getChannel().sendFile(new File(tempFilename), m.getId() + "_lastseen.txt").queue(message -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(Vars.deleteUserlogFullDelay * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    botBessage.get().editMessage("*This report has been deleted*").queue();
                    message.delete().complete();
                }).start();
            });

            // Delete the temp file
            new File(tempFilename).delete();
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
