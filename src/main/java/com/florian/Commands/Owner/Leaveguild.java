package com.florian.Commands.Owner;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Leaveguild extends BaseCommand {
    public Leaveguild() {
        super.command = "leaveguild";
        super.description = "Leaves a guild.";
        super.userType = UserType.OWNER;
        super.arguments = "<guild-id>";
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Try and get the guild to leave
            Guild g;
            try {
                g = e.getJDA().getGuildById(args[0].trim());
            } catch (Exception ex) {
                // Return UNKNOWN_ID because we couldn't get the guild
                return ErrorCode.UNKNOWN_ID;
            }

            // Also check that the guild isn't null
            if(g == null)
                return ErrorCode.UNKNOWN_ID;

            // Create an epic embed to tell the user the bot left the guild
            EmbedBuilder embed = Util.defaultEmbed();

            // Set the title
            embed.setTitle("Left guild \"" + g.getName() + "\"");

            // Fill in the embed
            embed.addField("ID", "`" + g.getId() + "`", false);
            embed.addField("Members", "`" + g.getMemberCount() + "`", false);

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();

            // Leave the guild
            //g.leave().queue();

            // Return success
            return ErrorCode.SUCCESS;
        } else {
            // No arguments, so cancel
            return ErrorCode.WRONG_ARGUMENTS;
        }
    }
}
