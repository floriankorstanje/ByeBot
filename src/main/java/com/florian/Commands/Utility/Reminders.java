package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Reminders.ReminderEntry;
import com.florian.Reminders.UserReminders;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Reminders extends BaseCommand {
    public Reminders() {
        super.command = "reminders";
        super.description = "Pings you in a set time to remind you of something.";
        super.arguments = "[operation(add/remove)] [time|reminder-id] [time-unit(days/hours/minutes/date)] [reminder]";
        super.examples.add("add 3 hours Make a discord bot");
        super.examples.add("add 12/3/2021 date Do something epic!");
        super.examples.add("remove 178217adda0");
        super.aliases.add("reminder");
        super.aliases.add("remindme");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if(args.length == 0) {
            // Get all reminders
            Pair<ReminderEntry[], ErrorCode> reminders = UserReminders.getReminders(e.getGuild(), e.getMember().getId());

            // Check if getReminders was successful. If not, return the error
            if(reminders.getRight() != ErrorCode.SUCCESS)
                return reminders.getRight();

            // Create embed to list reminders
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Reminders for " + e.getMember().getUser().getAsTag());

            // Fill the embed
            for(int i = 0; i < reminders.getLeft().length; i++) {
                ReminderEntry entry = reminders.getLeft()[i];

                // Add embed field
                embed.addField("Reminder `" + entry.getId() + "`", "Ends At: " + Util.formatDateTime(new Date(entry.getTime())) + "\nReminder: " + entry.getReason(), false);
            }

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if(args.length >= 2) {
            String operation = args[0];
            if(operation.equalsIgnoreCase("add")) {
                if(args.length < 3)
                    return ErrorCode.WRONG_ARGUMENTS;

                // Get arguments
                String time = args[1];
                String unit = args[2];
                StringBuilder reason = new StringBuilder();
                for(int i = 3; i < args.length; i++)
                    reason.append(args[i]).append(" ");

                // Get milliseconds to wait for
                long waitFor = 0;
                switch (unit.toLowerCase()) {
                    case "days":
                        waitFor = (long) Integer.parseInt(time) * 24 * 60 * 60 * 1000;
                        break;
                    case "hours":
                        waitFor = (long) Integer.parseInt(time) * 60 * 60 * 1000;
                        break;
                    case "minutes":
                        waitFor = (long) Integer.parseInt(time) * 60 * 1000;
                        break;
                    case "seconds":
                        waitFor = (long) Integer.parseInt(time) * 1000;
                        break;
                    case "date":
                        try {
                            waitFor = new SimpleDateFormat("d/M/yy").parse(time).toInstant().toEpochMilli() - Instant.now().toEpochMilli();
                        } catch (Exception ex) {
                            // Couldn't parse date
                            return ErrorCode.WRONG_ARGUMENTS;
                        }
                        break;
                    default:
                        return ErrorCode.WRONG_ARGUMENTS;
                }

                if(waitFor < 5000)
                    return ErrorCode.REMINDER_TOO_SHORT;

                // Get time
                long finalTime = Instant.now().toEpochMilli() + waitFor;

                // Add it to the list
                UserReminders.addReminder(e.getGuild(), e.getChannel(), e.getMember().getId(), finalTime, reason.toString());

                // Tell the user the reminder got added
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Added reminder for " + e.getMember().getUser().getAsTag());

                // Fill embed
                embed.addField("Time Done", Util.formatDateTime(new Date(finalTime)), false);
                embed.addField("Reminder", reason.toString(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if(operation.equalsIgnoreCase("remove")) {
                // Remove only takes 2 args
                if(args.length != 2)
                    return ErrorCode.WRONG_ARGUMENTS;

                // Get reminder ID
                String id = args[1];

                // Remove entry
                Pair<ReminderEntry, ErrorCode> removed = UserReminders.removeReminder(e.getGuild(), e.getMember().getId(), id);

                // Check if removal succeeded
                if(removed.getRight() != ErrorCode.SUCCESS)
                    return removed.getRight();

                // Removal was successful, so tell the user the reminder was removed
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Removed reminder `" + removed.getLeft().getId() + "`");

                // Fill embed
                embed.addField("Ends At", Util.formatDateTime(new Date(removed.getLeft().getTime())), false);
                embed.addField("Reminder", removed.getLeft().getReason(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong arguments
                return ErrorCode.WRONG_ARGUMENTS;
            }
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
