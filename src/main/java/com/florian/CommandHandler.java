package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.Config.BotConfig;
import com.florian.Config.GuildConfig;
import com.florian.DisabledCommands.DisabledCommands;
import com.florian.Log.Log;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler extends ListenerAdapter {
    private final Dictionary<String, Long> cooldowns = new Hashtable<>();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // Check if the message starts with the bot prefix
        if (event.getMessage().getContentRaw().startsWith(Vars.botPrefix) || event.getMessage().getContentRaw().startsWith(GuildConfig.getPrefix(event.getGuild()))) {
            // Check if the user is still on cooldown
            if (cooldowns.get(event.getMember().getId()) != null) {
                long expiresAt = cooldowns.get(event.getMember().getId()) + Vars.commandCooldown * 1000;
                long timeLeft = Math.round((expiresAt - Instant.now().toEpochMilli()) / 1000.0);
                event.getChannel().sendMessage("Please wait " + timeLeft + " second(s) before running a command again, " + event.getMember().getAsMention() + ".").queue();
                return;
            }

            // Make some variables to make accessing the command and arguments easier
            String[] msg = event.getMessage().getContentRaw().split("\\s+");
            String cmd = msg[0].replace(Vars.botPrefix, "").replace(GuildConfig.getPrefix(event.getGuild()), "");
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

            // Check if the command was recognized, if so, execute it
            BaseCommand command;
            try {
                command = Util.getCommandByName(cmd);
            } catch (Exception ignored) {
                handleError(ErrorCode.UNKNOWN_COMMAND, event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
                return;
            }

            // Check if the command is disabled
            if (DisabledCommands.isDisabled(event.getGuild(), command)) {
                handleError(ErrorCode.COMMAND_DISABLED, event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
                return;
            }

            // Check if the user that's trying to execute isn't executing an owner-only command as owner
            if (command.commandType == CommandType.OWNER && !event.getMember().getId().equals(Vars.botOwner.getId())) {
                handleError(ErrorCode.NO_PERMISSION, event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
                return;
            }

            // If the commands has no arguments but args > 0 or if the command requires arguments but args = 0, return an error
            if (command.requiredArguments && args.length == 0) {
                handleError(ErrorCode.WRONG_ARGUMENTS, event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
                return;
            }

            if (command.permission == null) {
                handleError(command.execute(event, args), event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
            } else {
                if (event.getMember().hasPermission(command.permission)) {
                    handleError(command.execute(event, args), event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
                } else {
                    handleError(ErrorCode.NO_PERMISSION, event.getChannel(), event.getGuild(), event.getMember(), cmd, args.length);
                }
            }
        }
    }

    private void handleError(ErrorCode error, TextChannel channel, Guild g, Member member, String cmd, int argsLength) {
        // Add user to cooldown list
        if (error == ErrorCode.SUCCESS) {
            // Add user to cooldown list
            cooldowns.put(member.getId(), Instant.now().toEpochMilli());

            // Wait for the amount of cooldown and then remove the user from the cooldown list
            new Thread(() -> {
                try {
                    Thread.sleep(Vars.commandCooldown * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cooldowns.remove(member.getId());
            }).start();

            // Increment command counter for bot and build
            GuildConfig.incrementCommandCounter(g);
            BotConfig.incrementCommandCounter();
        }

        // Tell the user the command failed if it did, otherwise don't output anything
        if (error != ErrorCode.SUCCESS && error != ErrorCode.UNKNOWN_COMMAND) {
            // Create an embed to show the error to the user
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title and color
            embed.setTitle("An error occurred");
            embed.setColor(0xFF0000);

            // Fill the embed
            embed.addField("Executor", member.getAsMention(), false);
            embed.addField("Command", "`" + Vars.botPrefix + cmd + " [" + argsLength + "]`", false);
            embed.addField("Bot Version", "`" + Vars.version + "`", false);
            embed.addField("Error", "`" + error.toString() + "`", false);
            embed.addField("Report Bug", "If you'd like to report this error as a bug, submit a new issue [here](https://github.com/flornian/ByeBot/issues)", false);

            // Send the embed
            channel.sendMessage(embed.build()).queue();
        }

        // Log executed command to console and logfile
        Log.log("[" + member.getId() + "] [" + g.getId() + "]: " + cmd + " [" + argsLength + "] -> " + error);
    }
}
