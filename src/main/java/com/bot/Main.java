package com.bot;

import com.bot.data.Data;
import com.bot.data.Secrets;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        loadProperties();

        GatewayDiscordClient client = DiscordClientBuilder.create(Secrets.token())
                .build()
                .login()
                .block();

        client.updatePresence(Presence.online(Activity.playing("#help"))).subscribe();

        BotThread botThread = new BotThread(client);
        botThread.start();

        boolean running = true;
        while(running) {
            Scanner in = new Scanner(System.in);
            String cmd = in.nextLine();
            if (cmd.equals("stop")) {
                botThread.close();
                try {
                    botThread.join();
                } catch (Exception e) {
                    System.out.println("Discord bot server shutdown!");
                }
                running = false;
            }
        }

    }

    private static void loadProperties() {
        String filename = "news_feed/news_topics.properties";
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream(filename)) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find " + filename);
                return;
            }

            prop.load(input);

            prop.forEach((key, value) -> {
                Data.getAvailableNewsFeeds().put((String) key, (String) value);
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
