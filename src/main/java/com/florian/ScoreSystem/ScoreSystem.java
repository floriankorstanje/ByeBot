package com.florian.ScoreSystem;

import com.florian.ErrorCode;
import com.florian.Log.Log;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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

                // Make sure the user isn't in the AFK channel if the guild has one
                if (e.getGuild().getAfkChannel() != null && e.getChannelJoined().getId().equals(e.getGuild().getAfkChannel().getId()))
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
        // Set the score
        setScore(g, user, getScore(g, user) + score);
    }

    public static void checkRole(Guild g, String user, int score) {
        // Get all role rewards
        Pair<RoleRewardEntry[], ErrorCode> roleRewards = RoleReward.getRoleRewards(g);

        // Make sure getRoleRewards succeeded
        if (roleRewards.getRight() != ErrorCode.SUCCESS)
            return;

        // Loop through all role rewards and give the user a new role if they have the score
        for (RoleRewardEntry entry : roleRewards.getLeft()) {
            Role role = g.getRoleById(entry.getRole());

            // Add the role to the user if they don't have it already and they have enough score
            if (score > entry.getScore() && !g.getMemberById(user).getRoles().contains(role)) {
                // Give role to user
                g.addRoleToMember(user, role).queue(null, (err) -> Log.log("[" + user + "] [" + g.getName() + "(" + g.getId() + ")] Unable to find reward role"));

                // Announce that the user got the role
                try {
                    g.getSystemChannel().sendMessage("Congratulations " + g.getMemberById(user).getAsMention() + " on reaching a score of " + entry.getScore() + "! You have been awarded the `" + g.getRoleById(entry.getRole()).getName() + "` role!").queue();
                } catch (Exception ignored) {
                    // Server doesn't have a system channel, so don't send the message
                }
                return;
            }

            // The user has the role, but they don't have enough score for it, so we remove it
            if (score < entry.getScore() && g.getMemberById(user).getRoles().contains(role)) {
                g.removeRoleFromMember(user, role).queue(null, (err) -> Log.log("[" + user + "] [" + g.getName() + "(" + g.getId() + ")] Unable to find reward role"));
            }
        }
    }

    public static ErrorCode setScore(Guild g, String user, int score) {
        // Make sure the score that is trying to be set isn't invalid
        if (score < 0 || score >= Integer.MAX_VALUE)
            return ErrorCode.INVALID_SCORE;

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
            return ErrorCode.OTHER_ERROR;
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
                    element.setAttribute("score", String.valueOf(score));
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
        Log.log("[" + user + "] [" + g.getId() + "]: Set Score -> " + score);

        // Check if user should get new role
        checkRole(g, user, score);

        // Return success
        return ErrorCode.SUCCESS;
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
                System.out.println(i);
            }
        }

        // Sort the array to get the leaderboard
        sortScore(userScores);

        // Get new array to return
        List<UserScore> leaderboard = new ArrayList<>();

        // Only get the last few elements in the array
        for (int i = userScores.length - places; i < userScores.length; i++) {
            if (i >= 0)
                leaderboard.add(userScores[i]);

            System.out.println("Places: " + places + " - Length: " + userScores.length);
        }

        return Pair.of(leaderboard.toArray(new UserScore[0]), ErrorCode.SUCCESS);
    }

    private static void sortScore(UserScore[] array) {
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

    }

    public static Pair<Integer, ErrorCode> getLeaderboardPosition(Guild g, String user) {
        // Get leaderboard
        Pair<UserScore[], ErrorCode> leaderboard = getLeaderboard(g, g.getMemberCount());

        // Make sure getLeaderboard succeeded
        if (leaderboard.getRight() != ErrorCode.SUCCESS)
            return Pair.of(-1, leaderboard.getRight());

        // Get user's position
        int position = leaderboard.getLeft().length;
        for (int i = 0; i < leaderboard.getLeft().length; i++) {
            if (leaderboard.getLeft()[i].getUser().equalsIgnoreCase(user)) {
                position = leaderboard.getLeft().length - i;
                break;
            }
        }

        // Return position and success
        return Pair.of(position, ErrorCode.SUCCESS);
    }

    public static String getCurrentRole(Guild g, String user) {
        // Get all rolerewards
        Pair<RoleRewardEntry[], ErrorCode> roleRewards = RoleReward.getRoleRewards(g);

        // Make sure getRoleRewards succeeded
        if (roleRewards.getRight() != ErrorCode.SUCCESS)
            return "Unknown";

        // Get user's current score
        int currentScore = getScore(g, user);

        // Save current highest role points and role
        int rolePoints = 0;
        String role = "Unknown";

        // Get role
        for (RoleRewardEntry entry : roleRewards.getLeft()) {
            if (entry.getScore() > rolePoints)
                rolePoints = entry.getScore();

            if (currentScore > rolePoints) {
                try {
                    role = g.getRoleById(entry.getRole()).getAsMention();
                } catch (Exception ex) {
                    role = "`[" + entry.getRole() + "]` (This role doesn't exist anymore)";
                }
            }
        }

        // Return role
        return role;
    }
}
