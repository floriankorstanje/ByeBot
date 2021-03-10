package com.florian;

import com.florian.Commands.Help.Help;
import com.florian.Userlog.UserEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private static final String tokenFile = Vars.botFolder + "token.txt";

    public static void main(String[] args) throws IOException, LoginException {
        // Output starting message
        System.out.println("Starting ByeBot v" + Vars.version);

        // Check if a folder for bot files exits. If not, create one
        File botFolder = new File(Vars.botFolder);
        if (!botFolder.exists()) {
            boolean success = botFolder.mkdirs();

            // If it wasn't successful, quit
            if (!success) {
                System.out.println("Unable to create bot folder. Quitting.");
                return;
            }
        }

        // Check if the folder for guild-specific files exists
        File guildsFolder = new File(Vars.guildsFolder);
        if (!guildsFolder.exists()) {
            boolean success = guildsFolder.mkdirs();

            // If it wasn't successful, quit
            if (!success) {
                System.out.println("Unable to create guilds folder. Quitting.");
                return;
            }
        }

        // Check if the file with a bot token exists. If not, create one
        if (!new File(tokenFile).exists()) {
            System.out.println("Please enter your bots token in \"" + tokenFile + "\" to start the bot.");
            Files.createFile(Paths.get(tokenFile));
            return;
        }

        // Check if the token file isn't empty. If the user entered a wrong token, JDA will output an error
        if (Util.readFile(tokenFile).size() == 0) {
            System.out.println("Please enter your bots token in \"" + tokenFile + "\" to start the bot.");
            return;
        }

        // Get the bot token from the token file
        String token = Util.readFile(tokenFile).get(0);

        // Initialize JDABuilder, specify GatewayIntents and add the event listeners
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_BANS);

        // Add all the event listeners
        builder.addEventListeners(new CommandHandler());
        builder.addEventListeners(new UserEvents());

        // Create JDA class and start the bot
        JDA jda = builder.build();

        // Set the bot presence
        jda.getPresence().setActivity(Activity.listening(Vars.botPrefix + new Help().command));

        // Set some variables
        Vars.appInfo = jda.retrieveApplicationInfo().complete();
        Vars.botOwner = Vars.appInfo.getOwner();
    }
}
