package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.Config.GuildConfig;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelpCommand extends BaseCommand {
    public HelpCommand() {
        super.command = "help";
        super.description = "Shows all the commands and how to use them.";
        super.arguments = "[command]";
        super.examples.add("invite");
        super.commandType = CommandType.UTILITY;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Make an embed because they look good
        EmbedBuilder embed = Util.defaultEmbed();

        // Check if the user wants more advanced info about a command by specifying it as an argument
        if (args.length == 1) {
            for (BaseCommand command : Vars.commands) {
                if (command.command.equalsIgnoreCase(args[0]) || Util.containsIgnoreCase(command.aliases, args[0])) {
                    // Set title
                    embed.setTitle("Command info for " + Vars.botPrefix + command.command);

                    // Add all the info about the command
                    embed.addField("Name", command.command, false);
                    embed.addField("Description", command.description, false);
                    embed.addField("Advanced Description", command.advancedDescription, false);
                    embed.addField("Usage", "`" + Vars.botPrefix + command.command + (command.arguments.length() > 0 ? " " + command.arguments + "`" : "`"), false);
                    embed.addField("Examples", command.examples.size() == 0 ? "none" : "`" + Vars.botPrefix + command.command + " " + String.join("`\n`" + Vars.botPrefix + command.command + " ", command.examples) + "`", false);
                    embed.addField("Aliases", command.aliases.size() == 0 ? "none" : "`" + String.join("` `", command.aliases) + "`", false);
                    embed.addField("Permission", command.permission == null ? "none" : "`" + command.permission.toString() + "`", false);

                    // Send the embed to the user
                    e.getChannel().sendMessage(embed.build()).queue();

                    // Return so it doesn't send the entire command list again
                    return ErrorCode.SUCCESS;
                }
            }

            // Command wasn't recognized. Tell the user
            return ErrorCode.WRONG_ARGUMENTS;
        } else if (args.length > 1) {
            // There's too many arguments, so return an error
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Set some basic info for the embed
        embed.setTitle("Help for " + e.getJDA().getSelfUser().getName());
        embed.addField("You can type `" + Vars.botPrefix + this.command + " " + this.arguments + "` to get more specific help about a command.\nThis guild's custom prefix is `" + GuildConfig.getPrefix(e.getGuild()) + "`", "", false);

        // StringBuilders for all the categories
        StringBuilder fun = new StringBuilder();
        StringBuilder utility = new StringBuilder();
        StringBuilder info = new StringBuilder();
        StringBuilder score = new StringBuilder();
        StringBuilder mod = new StringBuilder();
        StringBuilder owner = new StringBuilder();

        //FUN, UTILITY, INFO, MODERATION, OWNER

        // Add all the commands and their descriptions to the list
        for (BaseCommand command : Vars.commands) {
            // Add the commands to the right StringBuilder
            switch (command.commandType) {
                case FUN:
                    addToStringBuilder(fun, command);
                    break;
                case UTILITY:
                    addToStringBuilder(utility, command);
                    break;
                case INFO:
                    addToStringBuilder(info, command);
                    break;
                case SCORE:
                    addToStringBuilder(score, command);
                    break;
                case MODERATION:
                    addToStringBuilder(mod, command);
                    break;
                case OWNER:
                    addToStringBuilder(owner, command);
                    break;
            }
        }

        // Add commands to embed
        embed.addField("\uD83E\uDE9B Utility", utility.toString(), false);
        embed.addField("\u2139 Information", info.toString(), false);
        embed.addField("\uD83D\uDCAF Score", score.toString(), false);
        embed.addField("\uD83D\uDE04 Fun", fun.toString(), false);
        embed.addField("\uD83D\uDD27 Moderation", mod.toString(), false);
        embed.addField("\u2699  Owner", owner.toString(), false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }

    private void addToStringBuilder(StringBuilder builder, BaseCommand command) {
        builder.append("**").append(command.command).append("** - ").append(command.description).append("\n");
    }
}
