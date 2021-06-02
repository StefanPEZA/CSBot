package com.bot;

import com.bot.data.Data;
import com.bot.data.ManagerEntity;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ManagerEntity.getEm().createEntityManager();

        GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                .build()
                .login()
                .block();

        client.updatePresence(Presence.online(Activity.playing("#help"))).subscribe();

        BotThread botThread = new BotThread(client);
        botThread.start();

        boolean running = true;
        while (running) {
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
}
