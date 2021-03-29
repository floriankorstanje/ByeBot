package com.florian.Commands.Info;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AvatarCommand extends BaseCommand {
    public AvatarCommand() {
        super.command = "avatar";
        super.description = "Sends a user's avatar.";
        super.arguments = "[user]";
        super.commandType = CommandType.INFO;
        super.aliases.add("pfp");
        super.aliases.add("image");
        super.examples.add("399594813390848002");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length < 2) {
            // Get member
            Member member = e.getMember();

            // If there is a user args, try to parse that
            if (args.length == 1) {
                try {
                    member = e.getGuild().getMemberById(args[0]);
                } catch (Exception ex) {
                    return ErrorCode.UNKNOWN_ID;
                }
            }

            // Get profile picture and send it in an embed
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Avatar of " + member.getUser().getAsTag());

            // Set image
            embed.setImage(member.getUser().getAvatarUrl());

            // Send embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
