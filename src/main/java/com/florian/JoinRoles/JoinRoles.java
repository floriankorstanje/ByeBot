package com.florian.JoinRoles;

import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JoinRoles {
    public static ErrorCode addJoinRoles(Guild g, String[] roles) {
        String file = Util.getGuildFolder(g) + Vars.joinRolesFile;

        // Make sure file exists
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Get joinroles
        List<String> joinRoles;
        try {
            joinRoles = Util.readFile(file);
        } catch (IOException e) {
            return ErrorCode.OTHER_ERROR;
        }

        if (joinRoles.size() + roles.length > Vars.maxJoinRoles)
            return ErrorCode.TOO_MANY_JOINROLES;

        for (String role : roles) {
            if (!joinRoles.contains(role))
                joinRoles.add(role);
        }

        // Write changes back to file
        try {
            Util.writeFile(file, joinRoles);
        } catch (IOException e) {
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode removeJoinRoles(Guild g, String[] roles) {
        String file = Util.getGuildFolder(g) + Vars.joinRolesFile;

        // Make sure file exists
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                return ErrorCode.OTHER_ERROR;
            }
        }

        // Get joinroles
        List<String> joinRoles;
        try {
            joinRoles = Util.readFile(file);
        } catch (IOException e) {
            return ErrorCode.OTHER_ERROR;
        }

        for (String role : roles)
            joinRoles.remove(role);

        // Write changes back to file
        try {
            Util.writeFile(file, joinRoles);
        } catch (IOException e) {
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static ErrorCode clearJoinRoles(Guild g) {
        String file = Util.getGuildFolder(g) + Vars.joinRolesFile;

        // Make sure file exists
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                return ErrorCode.SUCCESS;
            }
        }

        // Write empty list to file
        try {
            Util.writeFile(file, new ArrayList<>());
        } catch (IOException e) {
            return ErrorCode.OTHER_ERROR;
        }

        return ErrorCode.SUCCESS;
    }

    public static String[] getJoinRoles(Guild g) {
        String file = Util.getGuildFolder(g) + Vars.joinRolesFile;

        // Make sure file exists
        if (!new File(file).exists()) {
            try {
                Files.createFile(Paths.get(file));
            } catch (IOException e) {
                return new String[]{};
            }
        }

        // Get joinroles and return
        try {
            return Util.readFile(file).toArray(new String[0]);
        } catch (IOException e) {
            return new String[]{};
        }
    }
}
