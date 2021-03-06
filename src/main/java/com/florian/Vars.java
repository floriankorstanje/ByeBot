package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.Moderation.*;
import com.florian.Commands.Utility.*;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.User;

public class Vars {
    // Files and folders the bot uses
    public static final String botFolder = System.getProperty("user.dir") + "/Bot-Files/";
    public static final String serversFolder = botFolder + "servers/";
    public static final String userlogFolder = "/userlogs/";
    public static final String historyFolder = "/history/";

    // Bot info
    public static final String botPrefix = "$";
    public static final String version = "0.4.5";
    public static ApplicationInfo appInfo;
    public static User botOwner;

    // Delays
    public static final int deleteUserlogFullDelay = 10;
    public static final int waitForPruneReactionDelay = 5;

    // Other
    public static final int maxUserlogEntries = 64;
    public static final int color = 0x890BEF;
    public static final BaseCommand[] commands = {new Help(), new Modhelp(), new Invite(), new Userinfo(), new Serverinfo(), new Servers(), new Botinfo(), new Ban(), new Kick(), new Warn(), new History(), new Lastseen(), new Hostinfo(), new Prune()};
}
