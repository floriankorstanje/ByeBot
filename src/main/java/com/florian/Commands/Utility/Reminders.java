package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Reminders extends BaseCommand {
    private static List<Reminder> reminders = new ArrayList<>();

    public Reminders() {
        super.command = "reminders";
        super.description = "Pings you in a set time to remind you of something.";
        super.arguments = "[operation(add/remove)] [time|reminder-id] [time-unit(hours/minutes/seconds)] [reminder]";
        super.examples.add("add 3 hours Make a discord bot");
        super.examples.add("remove 178217adda0");
        super.aliases.add("reminder");
        super.aliases.add("remindme");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if(args.length == 0) {
            // Embed to show all the reminders
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Reminders for " + e.getMember().getUser().getAsTag());

            // Counter
            int count = 0;

            // Fill the embed
            for(Reminder reminder : reminders) {
                if(reminder.getUser().equals(e.getMember().getId())) {
                    embed.addField("Reminder #" + count, "ID: `" + reminder.getId() + "`\nTime Remaining: `" + Util.formatTime(reminder.getTime() - Instant.now().toEpochMilli()) + "`\nReason: " + reminder.getReason(),false);
                    count++;
                }
            }

            // If there's no reminders, tell the user
            if(count == 0)
                return ErrorCode.NO_REMINDERS;

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if(args.length >= 2) {
            String operation = args[0];
            if(operation.equalsIgnoreCase("add")) {
                if(args.length < 3)
                    return ErrorCode.WRONG_ARGUMENTS;

                // Get arguments
                int time = Integer.parseInt(args[1]);
                String unit = args[2];
                StringBuilder reason = new StringBuilder();
                for(int i = 3; i < args.length; i++)
                    reason.append(args[i]).append(" ");

                // Get milliseconds to wait for
                long waitFor = 0;
                switch (unit.toLowerCase()) {
                    case "hours":
                        waitFor = (long) time * 60 * 60 * 1000;
                        break;
                    case "minutes":
                        waitFor = (long) time * 60 * 1000;
                        break;
                    case "seconds":
                        waitFor = (long) time * 1000;
                        break;
                    default:
                        return ErrorCode.WRONG_ARGUMENTS;
                }

                if(waitFor < 5000)
                    return ErrorCode.REMINDER_TOO_SHORT;

                // Get time
                long finalTime = Instant.now().toEpochMilli() + waitFor;

                // Add it to the list
                Reminder reminder = new Reminder(e.getMember().getId(), finalTime, reason.toString());
                reminders.add(reminder);

                // Tell the user the reminder got added
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Added reminder for " + e.getMember().getUser().getAsTag());

                // Fill embed
                embed.addField("ID", "`" + reminder.getId() + "`", false);
                embed.addField("Reminder", reminder.getReason(), false);

                // Send the embed
                e.getChannel().sendMessage(embed.build()).queue();

                // Create a thread to wait
                long finalWaitFor = waitFor;
                new Thread(() -> {
                    try {
                        Thread.sleep(finalWaitFor);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }

                    // Remove the reminder from the list
                    reminders.remove(reminder);

                    // Send message to tell the user
                    e.getChannel().sendMessage(e.getMember().getAsMention() + ", your reminder for \"" + reason.toString() + "\" is done.").queue();
                }).start();
            } else if(operation.equalsIgnoreCase("remove")) {
                // Remove only takes 2 args
                if(args.length != 2)
                    return ErrorCode.WRONG_ARGUMENTS;

                // Save the id
                String id = args[1];

                // Delete reminder if it matches
                boolean removed = reminders.removeIf(reminder -> reminder.getId().equalsIgnoreCase(id) && reminder.getUser().equals(e.getMember().getId()));

                // Tell the user if the reminder was removed
                if(removed) {
                    // Create embed
                    EmbedBuilder embed = Util.defaultEmbed();

                    // Set title
                    embed.setTitle("Removed reminder for " + e.getMember().getUser().getAsTag());

                    // Fill embed
                    embed.addField("ID", "`" + id + "`", false);
                } else {
                    // Return UNKNOWN_ENTRY if there was no reminder found
                    return ErrorCode.UNKNOWN_ENTRY;
                }
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

    // Class to save reminders
    private static class Reminder {
        private final String id;
        private final String user;
        private final long time;
        private final String reason;

        public Reminder(String user, long time, String reason) {
            this.id = Long.toHexString(Instant.now().toEpochMilli());
            this.user = user;
            this.time = time;
            this.reason = reason;
        }

        public String getId() {
            return id;
        }

        public String getUser() {
            return user;
        }

        public long getTime() {
            return time;
        }

        public String getReason() {
            return reason;
        }
    }
}
