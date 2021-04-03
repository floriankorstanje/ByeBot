package com.florian.ScoreSystem;

import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScoreSystem {
    public static ArrayList<Pair<Guild, Member>> sentMessage = new ArrayList<>();

    public static void messageScoreThread() {
        new Thread(() -> {
            while (true) {
                // Clear the list of member that sent a message previously
                sentMessage.clear();

                // Wait some time before giving Score to the users again
                try {
                    Thread.sleep(Vars.messageScoreDelay * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Add Score to the users that sent a message
                if (sentMessage != null) {
                    for (Pair<Guild, Member> m : sentMessage)
                        addScore(m.getLeft(), m.getRight().getId(), 1);
                }
            }
        }).start();
    }

    public static void voiceChannelScoreThread(GuildVoiceJoinEvent e) {
        new Thread(() -> {
            while (true) {
                // Make sure the user isn't a bot
                if (e.getMember().getUser().isBot())
                    return;

                // Sleep for some time before giving Score again
                try {
                    Thread.sleep(Vars.voiceScoreDelay * 1000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

                // Make sure the user isn't in the AFK channel
                if (e.getChannelJoined().getId().equals(e.getGuild().getAfkChannel().getId()))
                    return;

                // Make sure the user is still in the channel
                if (!e.getChannelJoined().getMembers().contains(e.getMember()))
                    return;

                // Make sure the user is not just in a channel with a bot
                if (Util.removeBots(e.getChannelJoined().getMembers()).size() <= 1)
                    return;

                // Make sure the user isn't deafened
                if (!e.getMember().getVoiceState().isDeafened())
                    addScore(e.getGuild(), e.getMember().getId(), 1);
            }
        }).start();
    }

    public static void addScore(Guild g, String user, int score) {
        // Get file path
        String file = Util.getGuildFolder(g) + Vars.scoreFile;

        // Check if the file exists
        if (!new File(file).exists()) {
            // Create file
            Util.createXmlFile(file, "scores");
        }

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return;
        }

        // Get all history entries
        NodeList entries = document.getElementsByTagName("entry");

        boolean hasEntrty = false;

        // Get one for the current user
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("user").equals(user)) {
                    hasEntrty = true;
                    element.setAttribute("score", String.valueOf(Integer.parseInt(element.getAttribute("score")) + score));
                    break;
                }
            }
        }

        // Check if an element was found, if not create
        if (!hasEntrty) {
            Element toAdd = document.createElement("entry");
            toAdd.setAttribute("user", user);
            toAdd.setAttribute("score", String.valueOf(score));
            document.getElementsByTagName("scores").item(0).appendChild(toAdd);
        }

        // Write changes back to file
        Util.writeXml(file, document);

        // Log
        Log.log("[" + user + "] [" + g.getName() + " (" + g.getId() + ")]: Add Score -> " + score);
    }

    public static int getScore(Guild g, String user) {
        // Get file path
        String file = Util.getGuildFolder(g) + Vars.scoreFile;

        // Check if the file exists
        if (!new File(file).exists())
            return 0;

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return 0;
        }

        // Get all score entries
        NodeList entries = document.getElementsByTagName("entry");

        // Get one for the current user
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getAttribute("user").equals(user))
                    return Integer.parseInt(element.getAttribute("score"));
            }
        }

        // Return 0 as default
        return 0;
    }

    public static Pair<UserScore[], ErrorCode> getLeaderboard(Guild g, int places) {
        // Get file location for score file
        String file = Util.getGuildFolder(g) + Vars.scoreFile;

        // Check if the file exists
        if (!new File(file).exists())
            return Pair.of(new UserScore[]{}, ErrorCode.NO_SCORES);

        // Get file
        File input = new File(file);
        Document document;

        // Try to parse existing entries
        try {
            document = Util.getDocBuilder().parse(input);
        } catch (Exception e) {
            // Failed to parse
            return Pair.of(new UserScore[]{}, ErrorCode.OTHER_ERROR);
        }

        // Get all score entries
        NodeList entries = document.getElementsByTagName("entry");

        // Create list for all users and their scores
        UserScore[] userScores = new UserScore[entries.getLength()];

        // Get one for the current user
        for (int i = 0; i < entries.getLength(); i++) {
            Node node = entries.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                userScores[i] = new UserScore(element.getAttribute("user"), Integer.parseInt(element.getAttribute("score")));
            }
        }

        // Sort the array to get the leaderboard
        userScores = sortScore(userScores);

        // Get new array to return
        List<UserScore> leaderboard = new ArrayList<>();

        // Only get the last few elements in the array
        for (int i = userScores.length - places; i < userScores.length; i++) {
            if (i >= 0)
                leaderboard.add(userScores[i]);
        }

        return Pair.of(leaderboard.toArray(new UserScore[0]), ErrorCode.SUCCESS);
    }

    private static UserScore[] sortScore(UserScore[] array) {
        UserScore temp;
        for (int i = 1; i < array.length; i++) {
            for (int j = i; j > 0; j--) {
                if (array[j].getScore() < array[j - 1].getScore()) {
                    temp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = temp;
                }
            }
        }

        return array;
    }
}
