package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.Fun.RollCommand;
import com.florian.Commands.Help.HelpCommand;
import com.florian.Commands.Help.ModhelpCommand;
import com.florian.Commands.Help.OwnerhelpCommand;
import com.florian.Commands.Moderation.*;
import com.florian.Commands.Owner.LeaveguildCommand;
import com.florian.Commands.Owner.StopCommand;
import com.florian.Commands.Utility.*;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.User;

import java.util.Random;

public class Vars {
    // Files and folders the bot uses
    public static final String botFolder = System.getProperty("user.dir") + "/Bot-Files/";
    public static final String guildsFolder = botFolder + "/guilds/";
    public static final String userlogFile = "/userlogs.xml";
    public static final String historyFile = "/history.xml";
    public static final String remindersFile = "/reminders.xml";
    public static final String guildConfigFile = "/config.xml";
    public static final String botConfigFile = botFolder + "bot.xml";

    // Bot info
    public static final String botPrefix = "$";
    public static final String version = "0.7.5";

    // Delays (Seconds)
    public static final int waitForPruneReactionDelay = 5;
    public static final int commandCooldown = 3;

    // Max entries
    public static final int maxUserlogEntries = 4;
    public static final int maxHistoryEntries = 16;
    public static final int maxReminderEntries = 16;
    public static final int maxGuildsInEmbed = 15;

    // Other
    public static final Random random = new Random();
    public static final int color = 0x890BEF;
    public static final BaseCommand[] commands = {new HelpCommand(), new ModhelpCommand(), new OwnerhelpCommand(), new InviteCommand(), new UserinfoCommand(), new GuildinfoCommand(), new GuildsCommand(), new BotinfoCommand(), new BanCommand(), new KickCommand(), new WarnCommand(), new HistoryCommand(), new LastseenCommand(), new HostinfoCommand(), new PruneCommand(), new LeaveguildCommand(), new StopCommand(), new PollCommand(), new RemindersCommand(), new RollCommand(), new SetprefixCommand()};
    public static ApplicationInfo appInfo;
    public static User botOwner;
}
