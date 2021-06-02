package com.bot;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

public class BotThread extends Thread {
    GatewayDiscordClient client;

    public BotThread(GatewayDiscordClient client) {
        this.client = client;
    }

    public void close() {
        this.interrupt();
    }

    @Override
    public void run() {
        client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
            final User self = event.getSelf();
            System.out.printf(
                    "Logged in as %s #%s%n", self.getUsername(), self.getDiscriminator()
            );
        });

        client.getEventDispatcher().on(MessageCreateEvent.class).subscribe(event -> {
            HandleCmdThread thread = new HandleCmdThread(client, event);
            thread.start();
        });

        try {
            client.onDisconnect().block();
        } catch (IllegalStateException e) {
            System.out.println("Discord bot server shutdown!");
        }
    }
}
