package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.ErrorCode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandHandler extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // Check if the message starts with the bot prefix
        if(event.getMessage().getContentRaw().startsWith(Vars.botPrefix)) {
            // Make some variables to make accessing the command and arguments easier
            String[] msg = event.getMessage().getContentRaw().split("\\s+");
            String cmd = msg[0].replace(Vars.botPrefix, "");
            String[] args = Util.removeElement(msg, 0);

            // Save the error code if the command returns one
            ErrorCode error = ErrorCode.UNKNOWN_COMMAND;
            BaseCommand commandExcecuted = new BaseCommand();

            // Check if the command was recognized, if so, execute it
            for(BaseCommand command : Vars.commands) {
                if(command.command.equalsIgnoreCase(cmd) || Util.containsIgnoreCase(command.aliases, cmd)) {
                    commandExcecuted = command;

                    // If the commands has no arguments but args > 0 or if the command requires arguments but args = 0, return an error
                    if((!command.optionalArguments && !command.requiredArguments && args.length != 0) || (command.requiredArguments) && args.length == 0) {
                        error = ErrorCode.WRONG_ARGUMENTS;
                        break;
                    }

                    if(command.permission == null) {
                        error = command.execute(event, args);
                    }
                    else {
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
            if(error != ErrorCode.SUCCESS && error != ErrorCode.UNKNOWN_COMMAND) {
                // Create an embed to show the error to the user
                EmbedBuilder embed = Util.defaultEmbed();

                // Fill the embed
                embed.setTitle("An error occurred");
                embed.addField("Executor", event.getMember().getAsMention(), false);
                embed.addField("Command", "`" + Vars.botPrefix + cmd + "`", false);
                embed.addField("Arguments", String.valueOf(args.length), false);
                embed.addField("Bot version", "`" + Vars.version + "`", false);
                embed.addField("Java version", "`" + System.getProperty("java.version") + "`", false);
                embed.addField("Error", "`" + error.toString() + "`", false);
                embed.addField("Report Bug", "If you'd like to report this error as a bug, submit a new issue [here](https://github.com/floriankorstanje/ByeBot/issues)", false);

                // Send the embed
                event.getChannel().sendMessage(embed.build()).queue();
            }
        }
    }
}
