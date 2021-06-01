package com.bot.cmds;

import com.bot.data.Data;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import org.reactivestreams.Publisher;
import reactor.netty.channel.ChannelOperations;

import java.time.Instant;
import java.util.List;

public class Clear extends Command {
    @Override
    public boolean execute(Object... args) {
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

        return true;
    }

    private void deleteMessages(Message msg, Integer amount) {
        TextChannel channel = (TextChannel) msg.getChannel().block();

        msg.delete().block();
        channel.getMessagesBefore(Snowflake.of(Instant.now())).take(amount).transform(channel::bulkDeleteMessages).subscribe();

    }
}
