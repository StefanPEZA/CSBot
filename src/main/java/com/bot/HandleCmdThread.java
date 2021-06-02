package com.bot;

import com.bot.cmds.*;
import com.bot.data.Data;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;

public class HandleCmdThread extends Thread {
    MessageCreateEvent event;
    GatewayDiscordClient client;

    public HandleCmdThread(GatewayDiscordClient client, MessageCreateEvent event) {
        this.event = event;
        this.client = client;
    }

    @Override
    public void run() {
        Guild server = event.getGuild().block();
        String prefix = Data.getPrefix(server.getId().asString());

        Message msg = event.getMessage();

        User user = msg.getAuthor().get();
        String username = user.getUsername();
        String id = user.getId().asString();
        if (!user.isBot() && msg.getContent().startsWith(prefix + "prefix")) {
            Command exec = new ChangePrefix();
            String[] args = msg.getContent().replaceFirst(prefix, "").trim().split(" ");
            if (args.length == 1) {
                exec.execute(msg);
            } else {
                exec.execute(msg, args[1]);
            }
        } else if (!user.isBot() && msg.getContent().startsWith(prefix + "clear")) {
            Command exec = new Clear();
            String[] args = msg.getContent().replaceFirst(prefix, "").trim().split(" ");
            if (args.length == 1) {
                msg.getChannel().block()
                        .createMessage("Usage: ```" + prefix + "clear <amount>```")
                        .block();
            } else {
                exec.execute(msg, args[1]);
            }
        } else if (!user.isBot() && msg.getContent().startsWith(prefix + "help")) {
            Command exec = new Help();
            exec.execute(msg);
        } else if (!user.isBot() && msg.getContent().startsWith(prefix + "ask")) {
            Command exec = new Ask();
            String query = msg.getContent().replaceFirst(prefix + " *ask", "").trim();
            exec.execute(msg, query);
        } else if (!user.isBot() && msg.getContent().startsWith(prefix + "news")) {
            String args = msg.getContent().replaceFirst(prefix + " *news", "").trim();
            String[] tokens = args.split("#");
            String topic = "";
            if (tokens.length > 1) {
                topic = tokens[1].trim();
            }

            Integer max = 0;
            try {
                String maxTemp = tokens[0].trim();
                if (maxTemp.equals("")) {
                    maxTemp = "0";
                }
                max = Integer.parseInt(maxTemp);
                Command exec = new GetNews();
                exec.execute(msg, max, topic);
            } catch (Exception e) {
                e.printStackTrace();
                msg.getChannel().block()
                        .createMessage("Available categories: " + Data.getAvailableNewsFeeds().keySet() + "\nUsage: ```" + prefix + "news [max] #[topic]```")
                        .block();
            }
        } else if (!user.isBot() && msg.getContent().startsWith(prefix + "stack_news")) {
            String args = msg.getContent().replaceFirst(prefix + " *stack_news", "").trim();

            String[] tokens = args.split("#");

            String tags = "";
            if (tokens.length > 1) {
                tags = tokens[1].trim();
            }

            Integer max = 0;
            try {
                max = Integer.parseInt(tokens[0].trim());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            Command exec = new StackNews();
            exec.execute(msg, max, tags);
        } else if (!user.isBot() && msg.getContent().startsWith(prefix + "stack")) {
            String args = msg.getContent().replaceFirst(prefix + " *stack", "").trim();

            int index = (args.lastIndexOf("#") == -1) ? args.length() : args.lastIndexOf("#");
            String tag = args.substring(index);

            Command exec = new Stack();

            args = args.substring(0, index);
            exec.execute(msg, args, tag);
        }
    }
}
