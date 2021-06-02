package com.bot.cmds;

import com.bot.data.RssFeed;
import discord4j.core.object.entity.Message;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class StackNews implements Command {
    @Override
    public void execute(Object... args) {
        Message msg = (Message) args[0];

        Integer max = (Integer) args[1];
        if (max == 0 || max > 9) {
            max = 9;
        }

        String tags = (String) args[2];
        tags = tags.replaceAll(",", " ");

        UriComponentsBuilder builder;

        if (!tags.trim().equals("")) {
            builder = UriComponentsBuilder.fromUriString("https://stackoverflow.com/feeds/tag")
                    .queryParam("tagnames", tags)
                    .queryParam("sort", "newest");
        } else {
            builder = UriComponentsBuilder.fromUriString("https://stackoverflow.com/feeds")
                    .queryParam("sort", "newest");
        }

        URI uri = builder.build().toUri();
        System.out.println("Sending request to: " + uri);

        RssFeed.sendRequest(msg, uri, max);
    }
}