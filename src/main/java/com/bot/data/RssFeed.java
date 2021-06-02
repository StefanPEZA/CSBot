package com.bot.data;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import org.jsoup.Jsoup;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RssFeed {
    public static void sendRequest(Message msg, URI uri, Integer max) {
        try {
            MessageChannel channel = msg.getChannel().block();
            Message mes = msg.getChannel().block()
                    .createMessage("Processing...")
                    .block();

            URL feedSource = uri.toURL();
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            List<?> entries = feed.getEntries();

            for (int i = 0; i < Math.min(max, entries.size()); i++) {
                SyndEntryImpl o = (SyndEntryImpl) entries.get(i);

                channel.createEmbed(embedCreateSpec -> {
                    User user = msg.getAuthor().orElse(msg.getClient().getSelf().block());
                    embedCreateSpec.setFooter(user.getUsername(), user.getAvatarUrl())
                            .setTitle(Jsoup.parse(o.getTitle()).wholeText())
                            .setUrl(o.getLink())
                            .setColor(Color.LIGHT_SEA_GREEN);

                    String description = o.getDescription().getValue();
                    description = description.substring(0, Math.min(600, (description.length() == 0) ? 0 : description.length() - 1));
                    description = Jsoup.parse(description).wholeText();

                    embedCreateSpec.setDescription(description.substring(0, Math.min(300, (description.length() == 0) ? 0 : description.length() - 1)) + " ...\n")
                            .setTimestamp(Instant.now());
                }).subscribe();
            }

            mes.delete().subscribe();

        } catch (FeedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
