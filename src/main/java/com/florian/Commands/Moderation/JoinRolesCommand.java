package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.JoinRoles.JoinRoles;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinRolesCommand extends BaseCommand {
    public JoinRolesCommand() {
        super.command = "joinroles";
        super.description = "Give users a role when they join the server.";
        super.permission = Permission.MANAGE_ROLES;
        super.arguments = "[add/remove/clear] [roles]";
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 0) {
            // Just list all the joinroles
            String[] joinRoles = JoinRoles.getJoinRoles(e.getGuild());

            // Create embed
            EmbedBuilder embed = Util.defaultEmbed();

            embed.setTitle("Join roles for " + e.getGuild().getName());
            embed.setDescription("Total: " + joinRoles.length);

            StringBuilder rolesString = new StringBuilder();

            for (String joinRole : joinRoles) {
                try {
                    rolesString.append(e.getGuild().getRoleById(joinRole).getAsMention());
                } catch (Exception ignored) {
                    rolesString.append("`[").append(joinRole).append("]`");
                }
            }

            embed.addField("Roles", rolesString.toString(), false);

            // Send message
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            // Remove all joinroles
            ErrorCode error = JoinRoles.clearJoinRoles(e.getGuild());

            if (error != ErrorCode.SUCCESS)
                return error;

            // Create embed
            EmbedBuilder embed = Util.defaultEmbed();
            embed.setTitle("Successfully cleared join roles");

            // Send message
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("add")) {
                // Get all roles to add
                String[] toAdd = new String[args.length - 1];
                System.arraycopy(args, 1, toAdd, 0, args.length - 1);

                // Add roles
                ErrorCode error = JoinRoles.addJoinRoles(e.getGuild(), toAdd);

                // Check error
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Tell user
                EmbedBuilder embed = Util.defaultEmbed();
                embed.setTitle("Successfully added join role(s)");

                e.getChannel().sendMessage(embed.build()).queue();
            } else if (args[0].equalsIgnoreCase("remove")) {
                // Get all roles to remove
                String[] toRemove = new String[args.length - 1];
                System.arraycopy(args, 1, toRemove, 0, args.length - 1);

                // Remove roles
                ErrorCode error = JoinRoles.removeJoinRoles(e.getGuild(), toRemove);

                // Check error
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Tell user
                EmbedBuilder embed = Util.defaultEmbed();
                embed.setTitle("Successfully removed join role(s)");

                e.getChannel().sendMessage(embed.build()).queue();
            } else {
                // Wrong args
                return ErrorCode.WRONG_ARGUMENTS;
            }
        } else {
            // Wrong args
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
