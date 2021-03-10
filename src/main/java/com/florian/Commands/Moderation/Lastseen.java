package com.florian.Commands.Moderation;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.UserType;
import com.florian.ErrorCode;
import com.florian.Userlog.Userlog;
import com.florian.Userlog.UserlogEntries;
import com.florian.Userlog.UserlogEntry;
import com.florian.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Date;

public class Lastseen extends BaseCommand {
    public Lastseen() {
        super.command = "lastseen";
        super.description = "Shows when I last saw this user do something in this guild.";
        super.arguments = "<user>";
        super.userType = UserType.MODERATOR;
        super.permission = Permission.KICK_MEMBERS;
        super.requiredArguments = true;
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length == 1) {
            // Save member
            Member m;

            // Try to get member from ID
            try {
                m = e.getGuild().retrieveMemberById(args[0]).complete();
            } catch (Exception ex) {
                return ErrorCode.UNKNOWN_ID;
            }

            // Get all the entries
            UserlogEntries entries = Userlog.getEntries(e.getGuild(), m);

            // If getting entries failed, return
            if (entries.getErrorCode() != ErrorCode.SUCCESS)
                return entries.getErrorCode();

            // Create an embed
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Lastseen " + m.getUser().getAsTag());

            // Get all entries and add to embed
            for (int i = 0; i < entries.getEntries().length; i++) {
                // Don't add more than 5 entries to the list
                if (i >= 5)
                    break;

                UserlogEntry entry = entries.getEntries()[i];

                // Add entry to the embed
                embed.addField("Entry #" + i, "Time: " + Util.formatDate(new Date(entry.getTime())) + "\nAction: `" + entry.getAction() + "`", false);
            }

            // Send the embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else {
            // Wrong arguments
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }
}
