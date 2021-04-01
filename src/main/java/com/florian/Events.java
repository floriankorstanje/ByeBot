package com.florian;

import com.florian.Commands.Utility.HelpCommand;
import com.florian.ScoreSystem.ScoreSystem;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.time.Instant;

public class Events extends ListenerAdapter {
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent e) {
        // When the bot joins, set the bots nickname with the prefix
        e.getGuild().getSelfMember().modifyNickname("[" + Vars.botPrefix + "] " + e.getJDA().getSelfUser().getName()).queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        // Start XP message thread
        ScoreSystem.messageScoreThread();

        // Make a thread to change the bots status every 10 seconds
        new Thread(() -> {
            int count = 0;
            while (true) {
                String status = Vars.botPrefix + new HelpCommand().command + " | ";

                // Check which status it is now
                switch (count) {
                    case 0:
                        // 0 shows the bot version
                        status += "v" + Vars.version;
                        break;
                    case 1:
                        // 1 shows the the amount of guilds the bot is in
                        status += e.getJDA().getGuilds().size() + " guilds";
                        break;
                    case 2:
                        // 1 shows the the amount of users in all the guilds
                        int users = 0;
                        for (Guild g : e.getJDA().getGuilds())
                            users += g.getMemberCount();
                        status += users + " users";
                        break;
                }

                // Set the bot presence
                e.getJDA().getPresence().setActivity(Activity.listening(status));

                // Increment counter
                count++;

                // Make sure counter doesn't go too high
                if (count > 2)
                    count = 0;

                // Wait for 10 seconds
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        // Make a thread to check if any reminders expired and notify the user
        new Thread(() -> {
            while (true) {
                // Check for a reminders file for each guild
                // Get file
                String file = Vars.botFolder + Vars.remindersFile;
                File input = new File(file);

                // Check if file exists
                if (input.exists()) {
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
                    for (int i = 0; i < entries.getLength(); i++) {
                        Node node = entries.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;
                            long time = Long.parseLong(element.getAttribute("time"));

                            if (Instant.now().toEpochMilli() > time) {
                                // Reminder is done, tell the user
                                String reason = element.getAttribute("reason");
                                String guild = element.getAttribute("guild");

                                // Get user ID
                                String user = ((Element) element.getParentNode()).getAttribute("user");
                                e.getJDA().getGuildById(guild).retrieveMemberById(user).queue(member -> {
                                    // Send message
                                    member.getUser().openPrivateChannel().complete().sendMessage(member.getAsMention() + ", your reminder for \"" + reason + "\" is done.").queue(null, err -> {
                                        // Do nothing, just here to handle the error
                                    });
                                });

                                // Remove the reminder from the file
                                element.getParentNode().removeChild(element);

                                reminded = true;
                            }
                        }
                    }

                    // Only write back to the file if a reminder got removed
                    if (reminded) {
                        // If a change was made, update the file
                        Util.writeXml(file, document);
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
