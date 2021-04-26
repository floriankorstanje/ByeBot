package com.florian.WordBlacklist;

import com.florian.Commands.Moderation.WordblacklistCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CheckWordEvents extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // If the user has perms to execute wordblacklist command ignore this
        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(new WordblacklistCommand().permission))
            return;

        if (WordBlacklist.checkMessage(event.getGuild(), event.getMessage().getContentRaw()) && !event.getMember().getUser().isBot()) {
            // Try to remove the message
            event.getMessage().delete().queue(msg -> {
                event.getChannel().sendMessage(event.getMember().getAsMention() + ", please don't use blacklisted words!").queue();
            }, err -> {
                event.getChannel().sendMessage(event.getMember().getAsMention() + ", I couldn't delete your message, but please don't use blacklisted words!").queue();
            });
        }
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        // If the user has perms to execute wordblacklist command ignore this
        if (Objects.requireNonNull(event.getMember()).getPermissions().contains(new WordblacklistCommand().permission))
            return;

        if (WordBlacklist.checkMessage(event.getGuild(), event.getMessage().getContentRaw()) && !event.getMember().getUser().isBot()) {
            // Try to remove the message
            event.getMessage().delete().queue(msg -> {
                event.getChannel().sendMessage(event.getMember().getAsMention() + ", please don't use blacklisted words!").queue();
            }, err -> {
                event.getChannel().sendMessage(event.getMember().getAsMention() + ", I couldn't delete your message, but please don't use blacklisted words!").queue();
            });
        }
    }
}
