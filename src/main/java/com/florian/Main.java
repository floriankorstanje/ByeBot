package com.florian;

import com.florian.Commands.Utility.HelpCommand;
import com.florian.Config.BotConfig;
import com.florian.Log.Log;
import com.florian.Userlog.UserEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.time.Instant;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        // Check if logs folder exists
        File logFolder = new File(Vars.logsFolder);
        if (!logFolder.exists()) {
            boolean success = logFolder.mkdirs();

            // If it wasn't successful, quit
            if (!success) {
                System.out.println("Unable to create log folder. Quitting.");
                return;
            }
        }

        // Set logging file
        Vars.logFile = Vars.logsFolder + "log-" + Instant.now().toEpochMilli() + ".txt";

        // Output starting message
        Log.log("Starting ByeBot v" + Vars.version);

        // Check if a folder for bot files exits. If not, create one
        File botFolder = new File(Vars.botFolder);
        if (!botFolder.exists()) {
            boolean success = botFolder.mkdirs();

            // If it wasn't successful, quit
            if (!success) {
                Log.log("Unable to create bot folder. Quitting.");
                return;
            }
        }

        // Check if the folder for guild-specific files exists
        File guildsFolder = new File(Vars.guildsFolder);
        if (!guildsFolder.exists()) {
            boolean success = guildsFolder.mkdirs();

            // If it wasn't successful, quit
            if (!success) {
                Log.log("Unable to create guilds folder. Quitting.");
                return;
            }
        }

        // Get token
        String token = BotConfig.getToken();

        // Initialize JDABuilder, specify GatewayIntents and add the event listeners
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_BANS);

        // Add all the event listeners
        builder.addEventListeners(new CommandHandler());
        builder.addEventListeners(new UserEvents());
        builder.addEventListeners(new Events());

        // Create JDA class and start the bot
        JDA jda = builder.build();

        // Set some variables
        Vars.appInfo = jda.retrieveApplicationInfo().complete();
        Vars.botOwner = Vars.appInfo.getOwner();
    }
}
