package com.bot.cmds;

import com.bot.data.Data;
import com.bot.data.ManagerEntity;
import com.bot.data.RssFeed;
import discord4j.core.object.entity.Message;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.net.URI;

public class GetNews implements Command {
    @Override
    public void execute(Object... args) {
        Message msg = (Message) args[0];

        Integer max = (Integer) args[1];
        if (max == 0 || max > 9) {
            max = 9;
        }

        String topic = (String) args[2];


        if (topic.trim().equals("")) {
            topic = "all";
        }

        if (Data.getAvailableNewsFeeds().containsKey(topic)) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(Data.getAvailableNewsFeeds().get(topic));

            URI uri = builder.build().toUri();
            System.out.println("Sending request to: " + uri);

            RssFeed.sendRequest(msg, uri, max);
            msg.getChannel().flatMap(channel -> channel.createMessage("Available categories: " + Data.getAvailableNewsFeeds().keySet())).subscribe();
        } else {
            msg.getChannel().flatMap(channel -> channel.createMessage("Category not found!\nAvailable categories: " + Data.getAvailableNewsFeeds().keySet())).subscribe();
        }
    }
}
