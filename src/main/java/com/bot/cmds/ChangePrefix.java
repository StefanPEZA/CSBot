package com.bot.cmds;

import com.bot.data.Data;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;

public class ChangePrefix extends Command {
    @Override
    public boolean execute(Object... args) {
        Message msg = (Message) args[0];
        Guild guild = msg.getGuild().block();
        if (args.length == 1) {
            msg.getChannel()
                    .flatMap(channel -> channel.createMessage("Current prefix is " + Data.getPrefix(guild.getId().asString())))
                    .subscribe();
        } else if (args.length == 2) {
            String prefix = ((String) args[1]);
            if (prefix.length() > 2) {
                msg.getChannel()
                        .flatMap(channel -> channel.createMessage("The prefix must be a maximum of 2 characters!")).subscribe();
            }

            Data.setPrefix(guild.getId().asString(), prefix);
            msg.getChannel()
                    .flatMap(channel -> channel.createMessage("You changed prefix to " + Data.getPrefix(guild.getId().asString()))).subscribe();
        }
        return true;
    }
}
