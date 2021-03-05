package com.florian;

import com.florian.Commands.BaseCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // Check if the message starts with the bot prefix
        if (event.getMessage().getContentRaw().startsWith(Vars.botPrefix)) {
            // Make some variables to make accessing the command and arguments easier
            String[] msg = event.getMessage().getContentRaw().split("\\s+");
            String cmd = msg[0].replace(Vars.botPrefix, "");
            String[] args = Util.removeElement(msg, 0);

            // Supported types to convert from ping to ID
            Message.MentionType[] types = {Message.MentionType.USER, Message.MentionType.CHANNEL, Message.MentionType.ROLE, Message.MentionType.EMOTE};

            // Check arguments if it contains a ping to something. If so, convert it to an ID
            for (int i = 0; i < args.length; i++) {
                for (Message.MentionType type : types) {
                    Matcher matcher = type.getPattern().matcher(args[i]);
                    if (matcher.matches()) {
                        Matcher nums = Pattern.compile("[^0-9]").matcher(args[i]);
                        args[i] = nums.replaceAll("");
                    }
                }

            }

            // Save the error code and command for error handling
            ErrorCode error = ErrorCode.UNKNOWN_COMMAND;

            // Check if the command was recognized, if so, execute it
            for (BaseCommand command : Vars.commands) {
                if (command.command.equalsIgnoreCase(cmd) || Util.containsIgnoreCase(command.aliases, cmd)) {
                    // If the commands has no arguments but args > 0 or if the command requires arguments but args = 0, return an error
                    if (command.requiredArguments && args.length == 0) {
                        error = ErrorCode.WRONG_ARGUMENTS;
                        break;
                    }

                    if (command.permission == null) {
                        error = command.execute(event, args);
                    } else {
                        if (event.getMember().hasPermission(command.permission)) {
                            error = command.execute(event, args);
                        } else {
                            error = ErrorCode.NO_PERMISSION;
                        }
                    }
                    break;
                }
            }

            // Tell the user the command failed if it did, otherwise don't output anything
            if (error != ErrorCode.SUCCESS && error != ErrorCode.UNKNOWN_COMMAND) {
                // Create an embed to show the error to the user
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title and color
                embed.setTitle("An error occurred");
                embed.setColor(0xFF0000);

                // Fill the embed
                embed.addField("Executor", event.getMember().getAsMention(), false);
                embed.addField("Command", "`" + Vars.botPrefix + cmd + "`", false);
                embed.addField("Arguments", String.valueOf(args.length), false);
                embed.addField("Bot Version", "`" + Vars.version + "`", false);
                embed.addField("Java Version", "`" + System.getProperty("java.version") + "`", false);
                embed.addField("Error", "`" + error.toString() + "`", false);
                embed.addField("Report Bug", "If you'd like to report this error as a bug, submit a new issue [here](https://github.com/floriankorstanje/ByeBot/issues)", false);

                // Send the embed
                event.getChannel().sendMessage(embed.build()).queue();
            }

            // Log executed command to console
            System.out.println("Command " + cmd + " returned error code " + error.toString() + " in " + event.getGuild().getName());
        }
    }
}
