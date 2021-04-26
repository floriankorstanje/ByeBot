package com.florian.ScoreSystem;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ScoreEvents extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        // Add user to the list to add score if they're not on the list yet
        try {
            if (!ScoreSystem.sentMessage.contains(Pair.of(event.getGuild(), event.getMember())) && !Objects.requireNonNull(event.getMember()).getUser().isBot())
                ScoreSystem.sentMessage.add(Pair.of(event.getGuild(), event.getMember()));
        } catch (NullPointerException ignored) {
        }
    }

    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        // Start a loop to give the user score while they're in the call
        ScoreSystem.voiceChannelScoreThread(event);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        // Give a joining user 1 point so they're on the leaderboard
        ScoreSystem.setScore(event.getGuild(), event.getMember().getId(), 1);
    }
}
