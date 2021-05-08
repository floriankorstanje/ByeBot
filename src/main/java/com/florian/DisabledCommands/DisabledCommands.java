package com.florian.DisabledCommands;

import com.florian.Commands.BaseCommand;
import com.florian.ErrorCode;
import net.dv8tion.jda.api.entities.Guild;

public class DisabledCommands {
    public static ErrorCode disableCommand(Guild g, BaseCommand command) {
        // Return success
        return ErrorCode.SUCCESS;
    }

    public static ErrorCode enableCommand(Guild g, BaseCommand command) {
        // Return success
        return ErrorCode.SUCCESS;
    }
}
