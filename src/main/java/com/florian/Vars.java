package com.florian;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.Utility.*;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.User;

public class Vars {
    // Files and folders the bot uses
    public static final String botFolder = System.getProperty("user.dir") + "/Bot-Files/";

    // Bot info
    public static final String botPrefix = "$";
    public static final String version = "0.0.2_1";
    public static ApplicationInfo appInfo;
    public static User botOwner;

    // Other
    public static final int color = 0x890BEF;
    public static final BaseCommand[] commands = new BaseCommand[] { new Help(), new Invite(), new Userinfo(), new Serverinfo(), new Servers() };
}
