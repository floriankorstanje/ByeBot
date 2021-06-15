package com.florian.JoinRoles;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinRolesEvents extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        String[] joinRoles = JoinRoles.getJoinRoles(event.getGuild());
        for (String role : joinRoles) {
            try {
                event.getGuild().addRoleToMember(event.getMember().getId(), event.getGuild().getRoleById(role)).complete();
            } catch (Exception ignored) {
            }
        }
    }
}
