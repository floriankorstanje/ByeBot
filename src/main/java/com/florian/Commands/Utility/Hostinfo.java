package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.TimeUnit;

public class Hostinfo extends BaseCommand {
    public Hostinfo() {
        super.command = "hostinfo";
        super.description = "Gives info about the bot host.";
        super.aliases.add("host");
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        // Create embed
        EmbedBuilder embed = Util.defaultEmbed();

        // Get runtime
        Runtime runtime = Runtime.getRuntime();

        // Get RAM usage
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();
        long used = total - free;

        // Set title
        embed.setTitle("Host info for " + Vars.appInfo.getName());

        // Fill the embed
        embed.addField("Java Version", "`" + System.getProperty("java.version") + "`", false);
        embed.addField("Memory Usage", "`" + Math.round((double) used / 1024.0) + "kB / " + Math.round((double) total / 1024.0) + "kB`", false);
        embed.addField("Uptime", "`" + Util.getUptime() + "`", false);
        embed.addField("Operating System", "`" + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ")`", false);

        // Send the embed
        e.getChannel().sendMessage(embed.build()).queue();

        // Return success
        return ErrorCode.SUCCESS;
    }
}
