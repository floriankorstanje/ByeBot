package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.Moderation.Ban;
import com.florian.Commands.Moderation.History;
import com.florian.Commands.Moderation.Kick;
import com.florian.Commands.Moderation.Warn;
import com.florian.Commands.Utility.*;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.User;

public class Vars {
    // Files and folders the bot uses
    public static final String botFolder = System.getProperty("user.dir") + "/Bot-Files/";
    public static final String serversFolder = botFolder + "servers/";
    public static final String userlogFile = "userlog.txt";
    public static final String historyFolder = "/history/";

    // Bot info
    public static final String botPrefix = "$";
    public static final String version = "0.0.2_9";
    public static ApplicationInfo appInfo;
    public static User botOwner;

    // Other
    public static final int maxUserlogEntries = 256;
    public static final int color = 0x890BEF;
    public static final BaseCommand[] commands = new BaseCommand[] { new Help(), new Invite(), new Userinfo(), new Serverinfo(), new Servers(), new Botinfo(), new Ban(), new Kick(), new Warn(), new History() };
}
