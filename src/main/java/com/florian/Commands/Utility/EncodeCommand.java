package com.florian.Commands.Utility;

import com.florian.Commands.BaseCommand;
import com.florian.Commands.CommandType;
import com.florian.ErrorCode;
import com.florian.Util;
import com.florian.Vars;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncodeCommand extends BaseCommand {
    public EncodeCommand() {
        super.command = "encode";
        super.description = "Encodes text to a few encodings.";
        super.commandType = CommandType.UTILITY;
        super.requiredArguments = true;
        super.arguments = "<text>";
    }

    @Override
    public ErrorCode execute(GuildMessageReceivedEvent e, String[] args) {
        if (args.length > 0) {
            StringBuilder toEncodeBuilder = new StringBuilder();
            for (String arg : args)
                toEncodeBuilder.append(arg).append(" ");

            String toEncode = toEncodeBuilder.toString();

            // Make sure string isn't too long
            if (toEncode.length() > Vars.maxEncodeStringLength)
                return ErrorCode.INPUT_TOO_LONG;

            // Encode the text in a few ways
            String hex = toHex(toEncode);
            String base64 = toBase64(toEncode);
            String MD5 = toMD5(toEncode);

            // Create embed to show results
            EmbedBuilder embed = Util.defaultEmbed();

            // Set title
            embed.setTitle("Encoded text");

            // Fill info
            embed.addField("Results:", "Hex: `" + hex + "`\nBase64: `" + base64 + "`\nMD5: `" + MD5 + "`", false);

            // Send embed
            e.getChannel().sendMessage(embed.build()).queue();
        } else {
            // Wrong args
            return ErrorCode.WRONG_ARGUMENTS;
        }

        // Return success
        return ErrorCode.SUCCESS;
    }

    private String toHex(String in) {
        StringBuffer buffer = new StringBuffer();
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; i++)
            buffer.append(Integer.toHexString(chars[i]));

        return buffer.toString();
    }

    private String toBase64(String in) {
        return Base64.getEncoder().encodeToString(in.getBytes());
    }

    private String toMD5(String in) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(in.getBytes());
            byte[] digest = md.digest();
            return DatatypeConverter.printHexBinary(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "[ERROR]";
    }
}
