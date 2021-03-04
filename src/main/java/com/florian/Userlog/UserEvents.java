package com.florian.Userlog;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class UserEvents extends ListenerAdapter {
    // Message events
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    @Override
    public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    @Override
    public void onGuildMessageReactionRemove(@NotNull GuildMessageReactionRemoveEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    // Voice channel events
    @Override
    public void onGuildVoiceJoin(@NotNull GuildVoiceJoinEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }

    // Join event
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Userlog.addEvent(event.getGuild(), event.getMember(), event.getClass().getSimpleName());
    }
}
