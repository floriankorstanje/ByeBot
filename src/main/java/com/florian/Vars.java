package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.Fun.AskCommand;
import com.florian.Commands.Fun.CatCommand;
import com.florian.Commands.Fun.RollCommand;
import com.florian.Commands.Fun.SomeoneCommand;
import com.florian.Commands.Info.*;
import com.florian.Commands.Moderation.*;
import com.florian.Commands.Owner.ClearlogsCommand;
import com.florian.Commands.Owner.LeaveguildCommand;
import com.florian.Commands.Owner.SetstatusCommand;
import com.florian.Commands.Owner.StopCommand;
import com.florian.Commands.Score.LeaderboardCommand;
import com.florian.Commands.Score.ScoreCommand;
import com.florian.Commands.Utility.HelpCommand;
import com.florian.Commands.Utility.PollCommand;
import com.florian.Commands.Utility.RemindersCommand;
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
    public static final String logsFolder = botFolder + "logs/";
    public static final String scoreFile = "scores.xml";
    public static final String roleRewardsFile = "rolerewards.xml";
    public static final String blacklistedWordsFile = "blacklistedwords.txt";
    public static final String disabledCommandsFile = "disabledcommands.txt";
    public static String logFile = logsFolder + "botlog.txt";

    // Bot info
    public static final String botPrefix = "$";
    public static final String version = "0.11.5";
    public static String customStatus = "";

    // Delays (Seconds)
    public static final int waitForClearReactionDelay = 5;
    public static final int waitForPruneReactionDelay = 5;
    public static final int messageScoreDelay = 60;
    public static final int voiceScoreDelay = 300;
    public static final int commandCooldown = 3;

    // Max entries
    public static final int maxUserlogEntries = 4;
    public static final int maxHistoryEntries = 16;
    public static final int maxReminderEntries = 16;
    public static final int maxGuildsInEmbed = 15;
    public static final int maxPrefixLength = 16;
    public static final int maxLeaderboardPlaces = 10;
    public static final int maxRoleRewardEntries = 16;

    // Other
    public static final Random random = new Random();
    public static final int color = 0x890BEF;
    public static final BaseCommand[] commands = {new HelpCommand(), new UserinfoCommand(), new GuildinfoCommand(), new GuildsCommand(), new BotinfoCommand(), new BanCommand(), new KickCommand(), new WarnCommand(), new HistoryCommand(), new LastseenCommand(), new HostinfoCommand(), new ClearCommand(), new LeaveguildCommand(), new StopCommand(), new PollCommand(), new RemindersCommand(), new RollCommand(), new SetprefixCommand(), new ClearlogsCommand(), new PruneCommand(), new AvatarCommand(), new AskCommand(), new ScoreCommand(), new LeaderboardCommand(), new WordblacklistCommand(), new CatCommand(), new RolerewardCommand(), new ChangescoreCommand(), new SetstatusCommand(), new DisabledcommandsCommand(), new SomeoneCommand()};
    public static ApplicationInfo appInfo;
    public static User botOwner;
}
