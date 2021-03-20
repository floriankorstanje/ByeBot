package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Reminders.ReminderEntry;
import com.florian.Reminders.Reminders;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RemindersCommand extends BaseCommand {
    public RemindersCommand() {
        super.command = "reminders";
        super.description = "Pings you in a set time to remind you of something.";
        super.arguments = "[operation(add/remove/clear)] [time|reminder-id] [time-unit(days/hours/minutes/date/time/datetime)] [reminder]";
        super.examples.add("add 3 hours Make a discord bot");
        super.examples.add("add 12/3/2021 date Do something epic!");
        super.examples.add("add 09:05 time Enter the void");
        super.examples.add("add 17/3/2021-09:33 datetime Add datetime to reminders");
        super.examples.add("remove 178217adda0");
        super.examples.add("clear");
        super.aliases.add("reminder");
        super.aliases.add("remindme");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 0) {
            // Get all reminders
            Pair<ReminderEntry[], ErrorCode> reminders = Reminders.getReminders(e.getGuild(), e.getMember().getId());

            // Check if getReminders was successful. If not, return the error
            if (reminders.getRight() != ErrorCode.SUCCESS)
                return reminders.getRight();

            // Create embed to list reminders
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Reminders for " + e.getMember().getUser().getAsTag());

            // Fill the embed
            for (int i = 0; i < reminders.getLeft().length; i++) {
                ReminderEntry entry = reminders.getLeft()[i];

                // Add embed field
                embed.addField("Reminder `" + entry.getId() + "`", "Ends At: " + Util.formatDateTime(new Date(entry.getTime())) + "\nReminder: " + entry.getReason(), false);
            }

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length == 1) {
            // Check if user wants to clear all reminders
            if (args[0].equalsIgnoreCase("clear")) {
                // Remove all reminders and get count and error back
                Pair<Integer, ErrorCode> result = Reminders.clearReminders(e.getGuild(), e.getMember().getId());

                // Check if clearReminders succeeded, if not, return the error
                if (result.getRight() != ErrorCode.SUCCESS)
                    return result.getRight();

                // Create embed to tell user their reminders got removed
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Removed " + result.getLeft() + " reminders from " + e.getMember().getUser().getAsTag());

                // Set embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong args
                return ErrorCode.WRONG_ARGUMENTS;
            }
        } else {
            String operation = args[0];
            if (operation.equalsIgnoreCase("add")) {
                if (args.length < 3)
                    return ErrorCode.WRONG_ARGUMENTS;

                // Get arguments
                String time = args[1];
                String unit = args[2];
                StringBuilder reason = new StringBuilder();
                for (int i = 3; i < args.length; i++)
                    reason.append(args[i]).append(" ");

                // Save when reminder is done
                long timeDone = 0;

                // Get milliseconds to wait for
                switch (unit.toLowerCase()) {
                    case "days":
                        timeDone = (long) (Double.parseDouble(time) * 24 * 60 * 60 * 1000);

                        // Add current time
                        timeDone += Instant.now().toEpochMilli();
                        break;
                    case "hours":
                        timeDone = (long) (Double.parseDouble(time) * 60 * 60 * 1000);

                        // Add current time
                        timeDone += Instant.now().toEpochMilli();
                        break;
                    case "minutes":
                        timeDone = (long) (Double.parseDouble(time) * 60 * 1000);

                        // Add current time
                        timeDone += Instant.now().toEpochMilli();
                        break;
                    case "seconds":
                        timeDone = (long) (Double.parseDouble(time) * 1000);

                        // Add current time
                        timeDone += Instant.now().toEpochMilli();
                        break;
                    case "date":
                        try {
                            // Add 12 hours to the date so people don't get a ping at 12 o'clock midnight
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new SimpleDateFormat("d/M/yy").parse(time));
                            calendar.add(Calendar.HOUR_OF_DAY, 12);

                            // Get amount of milliseconds to wait for
                            timeDone = calendar.getTime().toInstant().toEpochMilli();
                        } catch (Exception ex) {
                            // Couldn't parse date
                            return ErrorCode.WRONG_ARGUMENTS;
                        }
                        break;
                    case "time":
                        try {
                            // Get date from input
                            Date date = new SimpleDateFormat("HH:mm").parse(time);

                            // Get calendar from date
                            Calendar calendar = GregorianCalendar.getInstance();
                            calendar.setTime(date);

                            // Get epoch of today midnight
                            Calendar finalTime = new GregorianCalendar();
                            finalTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                            finalTime.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                            finalTime.set(Calendar.SECOND, 0);
                            finalTime.set(Calendar.MILLISECOND, 0);

                            // Get amount of milliseconds to wait
                            timeDone = finalTime.toInstant().toEpochMilli();
                        } catch (Exception ex) {
                            // Couldn't parse date
                            return ErrorCode.WRONG_ARGUMENTS;
                        }
                        break;
                    case "datetime":
                        try {
                            // Parse date and time from argument
                            Date date = new SimpleDateFormat("d/M/yy-HH:mm").parse(time);

                            // Get epoch of this reminder's end time
                            timeDone = date.toInstant().toEpochMilli();
                        } catch (Exception ex) {
                            // Couldn't parse date
                            return ErrorCode.WRONG_ARGUMENTS;
                        }
                        break;
                    default:
                        return ErrorCode.WRONG_ARGUMENTS;
                }

                if (timeDone - Instant.now().toEpochMilli() < 0)
                    return ErrorCode.REMINDER_TOO_SHORT;

                // Add it to the list
                ErrorCode error = Reminders.addReminder(e.getGuild(), e.getChannel(), Util.generateId(), e.getMember().getId(), timeDone, reason.toString());

                // If addReminders didn't succeed, return the error
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Tell the user the reminder got added
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Added reminder for " + e.getMember().getUser().getAsTag());

                // Fill embed
                embed.addField("Time Done", Util.formatDateTime(new Date(timeDone)), false);
                embed.addField("Reminder", reason.toString(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if (operation.equalsIgnoreCase("remove")) {
                // Remove only takes 2 args
                if (args.length != 2)
                    return ErrorCode.WRONG_ARGUMENTS;

                // Get reminder ID
                String id = args[1];

                // Remove entry
                Pair<ReminderEntry, ErrorCode> removed = Reminders.removeReminder(e.getGuild(), e.getMember().getId(), id);

                // Check if removal succeeded
                if (removed.getRight() != ErrorCode.SUCCESS)
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
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
