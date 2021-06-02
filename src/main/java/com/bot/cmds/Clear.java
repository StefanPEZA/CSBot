package com.bot.cmds;

import com.bot.data.Data;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;

import java.time.Instant;

public class Clear implements Command {
    @Override
    public void execute(Object... args) {
        Message msg = (Message) args[0];
        String tempAmount = (String) args[1];

        Guild guild = msg.getGuild().block();
        String prefix = Data.getPrefix(guild.getId().asString());

        Integer amount;
        try {
            amount = Integer.parseInt(tempAmount);
        } catch (Exception e) {
            amount = 0;
            msg.getChannel().block()
                    .createMessage("Usage: ```" + prefix + "clear <amount>```")
                    .block();
        }

        deleteMessages(msg, amount);
    }

    private void deleteMessages(Message msg, Integer amount) {
        TextChannel channel = (TextChannel) msg.getChannel().block();

        msg.delete().block();
        channel.getMessagesBefore(Snowflake.of(Instant.now())).take(amount).transform(channel::bulkDeleteMessages).subscribe();

    }
}
