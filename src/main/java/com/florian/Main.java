package com.florian;

import com.florian.Commands.Help.HelpCommand;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

public class Main extends ListenerAdapter {
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
        builder.addEventListeners(new Main());

        // Create JDA class and start the bot
        JDA jda = builder.build();

        // Set the bot presence
        jda.getPresence().setActivity(Activity.listening(Vars.botPrefix + new HelpCommand().command));

        // Set some variables
        Vars.appInfo = jda.retrieveApplicationInfo().complete();
        Vars.botOwner = Vars.appInfo.getOwner();
    }

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        // Make a thread to check if any reminders expired and notify the user
        new Thread(() -> {
            while(true) {
                // Check for a reminders file for each guild
                for (Guild g : e.getJDA().getGuilds()) {
                    // Get file
                    String file = Util.getGuildFolder(g) + Vars.remindersFile;
                    File input = new File(file);

                    // Check if file exists
                    if(input.exists()) {
                        Document document;

                        // Try to parse existing entries
                        try {
                            document = Util.getDocBuilder().parse(input);
                        } catch (Exception ex) {
                            continue;
                        }

                        // Get all reminders
                        NodeList entries = document.getElementsByTagName("entry");

                        // Keep track if a reminder was done
                        boolean reminded = false;

                        // Loop through all reminder entries and check if they're done
                        for(int i = 0; i < entries.getLength(); i++) {
                            Node node = entries.item(i);
                            if(node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;
                                long time = Long.parseLong(element.getAttribute("time"));

                                if(Instant.now().toEpochMilli() > time) {
                                    // Reminder is done, tell the user
                                    String channel = element.getAttribute("channel");
                                    String reason = element.getAttribute("reason");

                                    // Get user ID
                                    String user = ((Element) element.getParentNode()).getAttribute("user");
                                    g.retrieveMemberById(user).queue(member -> {
                                        // Send message
                                        g.getTextChannelById(channel).sendMessage(member.getAsMention() + ", your reminder for \"" + reason + "\" is done.").queue();
                                    });

                                    // Remove the reminder from the file
                                    element.getParentNode().removeChild(element);

                                    reminded = true;
                                }
                            }
                        }

                        // Only write back to the file if a reminder got removed
                        if(reminded) {
                            // If a change was made, update the file
                            Util.writeXml(file, document);
                        }
                    }
                }

                // Wait for 5 seconds
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
