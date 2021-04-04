package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.WordBlacklist.WordBlacklist;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

public class WordblacklistCommand extends BaseCommand {
    public WordblacklistCommand() {
        super.command = "wordblacklist";
        super.description = "Makes a word blacklisted so when users use them they will be told not to use the word.";
        super.arguments = "[add/remove/clear] [words]";
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.MESSAGE_MANAGE;
        super.examples.add("add word");
        super.examples.add("add word1 word2 word3");
        super.examples.add("remove word");
        super.examples.add("remove word1 word2 word3");
        super.aliases.add("bannedwords");
        super.aliases.add("wordsblacklist");
        super.aliases.add("blacklistedwords");
        super.aliases.add("badwords");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if(args.length == 0) {
            // Get all blacklisted words
            Pair<String[], ErrorCode> blacklistedWords = WordBlacklist.getBlacklistedWords(e.getGuild());

            // Make sure getBlacklistedWords succeeded
            if(blacklistedWords.getRight() != ErrorCode.SUCCESS)
                return blacklistedWords.getRight();

            // Get amount of blacklisted words
            int total = blacklistedWords.getLeft().length;

            // Add all the words to one string
            StringBuilder words = new StringBuilder();
            for(String word : blacklistedWords.getLeft())
                words.append("`").append(word).append("` ");

            // Create embed to send
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Blacklisted words for " + e.getGuild().getName());

            // Fill embed
            embed.addField("Total: " + total, words.toString(), false);

            // Send embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if(args.length == 1) {
            // Make sure the user really wants to clear the list
            if(args[0].equalsIgnoreCase("clear")) {
                ErrorCode error = WordBlacklist.clearBlacklistedWords(e.getGuild());

                // Make sure clearBlacklistedWords succeeded
                if(error != ErrorCode.SUCCESS)
                    return error;

                // Create embed to tell user operation was successful
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully cleared blacklisted words list");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong args
                return ErrorCode.WRONG_ARGUMENTS;
            }
        } else {
            // Get what the user wants to do
            String operation = args[0];

            // Get all the words in an array
            String[] words = new String[args.length - 1];
            System.arraycopy(args, 1, words, 0, args.length - 1);

            if(operation.equalsIgnoreCase("add")) {
                // Add the words to the blacklisted words list
                ErrorCode error = WordBlacklist.addBlacklistedWords(e.getGuild(), words);

                // Make sure the operation succeeded
                if(error != ErrorCode.SUCCESS)
                    return error;

                // Create embed to tell the user the words were added
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully added " + words.length + " word(s) to the list.");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else if(operation.equalsIgnoreCase("remove")) {
                // Try to remove the words
                ErrorCode error = WordBlacklist.removeBlacklistedWords(e.getGuild(), words);

                // Make sure removeBlacklistedWords succeeded
                if(error != ErrorCode.SUCCESS)
                    return error;

                // Create embed to tell user if words weren't removed
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Successfully removed " + words.length + " word(s)");

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong args
                return ErrorCode.WRONG_ARGUMENTS;
            }
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
