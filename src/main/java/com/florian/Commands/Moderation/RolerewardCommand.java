package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.ScoreSystem.RoleReward;
import com.florian.ScoreSystem.RoleRewardEntry;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

public class RolerewardCommand extends BaseCommand {
    public RolerewardCommand() {
        super.command = "rolereward";
        super.description = "Adds a role to reward to people when they reach a certain score.";
        super.arguments = "[add/remove] [score/id] [role]";
        super.examples.add("");
        super.examples.add("add 2000 @coolpeople");
        super.examples.add("add 5000 818605390786592780");
        super.examples.add("remove 178e36deb55");
        super.aliases.add("rolerewards");
        super.commandType = CommandType.MODERATION;
        super.permission = Permission.MANAGE_ROLES;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 0) {
            // Get a list of all the rolerewards
            Pair<RoleRewardEntry[], ErrorCode> roles = RoleReward.getRoleRewards(e.getGuild());

            // Make sure getRoleRewards succeeded
            if (roles.getRight() != ErrorCode.SUCCESS)
                return roles.getRight();

            // Create embed to show all the rolerewards
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Role Rewards for " + e.getGuild().getName());

            // Add all the entries
            for (RoleRewardEntry entry : roles.getLeft()) {
                // Try to get the role as mention
                String role = "`[" + entry.getRole() + "]`";
                try {
                    role = e.getGuild().getRoleById(entry.getRole()).getAsMention();
                } catch (Exception ignored) {
                }

                // Add to embed
                embed.addField("Role Reward `" + entry.getId() + "`", "Score: `" + entry.getScore() + "`\nRole: " + role, false);
            }

            // Send embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else if (args.length == 2) {
            // Make sure the user actually meant to remove a rolereward
            if (args[0].equalsIgnoreCase("remove")) {
                // Try to remove the rolereward
                Pair<RoleRewardEntry, ErrorCode> deleted = RoleReward.removeRoleReward(e.getGuild(), args[1]);

                // Make sure removeRoleReward succeeded
                if (deleted.getRight() != ErrorCode.SUCCESS)
                    return deleted.getRight();

                // Try to get the role as mention
                String role = "`[" + deleted.getLeft().getRole() + "]`";
                try {
                    role = e.getGuild().getRoleById(deleted.getLeft().getRole()).getAsMention();
                } catch (Exception ignored) {
                }

                // Create embed to tell user that rolereward was removed
                EmbedBuilder embed = Util.defaultEmbed();

                // Set title
                embed.setTitle("Removed Role Reward");

                // Fill embed
                embed.addField("Role Reward `" + deleted.getLeft().getId() + "`", "Score: `" + deleted.getLeft().getScore() + "`\nRole: " + role, false);

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            }
        } else if (args.length == 3) {
            // Make sure the user actually meant to add a rolereward
            if (args[0].equalsIgnoreCase("add")) {
                // Get info needed to add
                int score = Integer.parseInt(args[1]);
                String roleId = args[2];

                // Try to add
                ErrorCode error = RoleReward.addRoleReward(e.getGuild(), score, roleId);

                // Make sure addRoleReward succeeded
                if (error != ErrorCode.SUCCESS)
                    return error;

                // Try to get the role as mention
                String role = "`[" + roleId + "]`";
                try {
                    role = e.getGuild().getRoleById(roleId).getAsMention();
                } catch (Exception ignored) {
                }

                // Create embed to tell user that rolereward was added
                EmbedBuilder embed = Util.defaultEmbed();

                // Fill embed
                embed.addField("Added Role Reward", "Score: `" + score + "`\nRole: " + role, false);

                // Send embed
                e.getChannel().sendMessage(embed.build()).queue();
            }
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        return ErrorCode.SUCCESS;
    }
}
