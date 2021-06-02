package com.bot.cmds;

import com.bot.data.Data;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Color;

public class Help implements Command {
    private final String title = "Here is a list of commands";
    private final StringBuilder description = new StringBuilder();

    @Override
    public void execute(Object... args) {
        short count = 1;
        Message msg = (Message) args[0];
        description.append("----------------------------------------------------------------\n");
        for (String cmd : Data.getAvailableCommands()) {
            description.append(count++).append(". ").append(Data.getPrefix(msg.getGuildId().orElse(Snowflake.of("1")).asString())).append(cmd);
            switch (cmd) {
                case "help":
                    description.append(" - get a list of available commands.");
                    break;
                case "prefix":
                    description.append(" [new prefix] - get or set a new prefix for the bot.");
                    break;
                case "news":
                    description.append(" [limit] #[topic] - get a list of news from RSS feeds on a certain topic.");
                    break;
                case "stack":
                    description.append(" <query> #[tag] - search questions on stackoverflow.");
                    break;
                case "stack_news":
                    description.append(" [limit] #[tags] - search for newest questions on stackoverflow.");
                    break;
                case "ask":
                    description.append(" <query> - get answers for almost all your questions using Wolfram Alpha");
                    break;
                case "clear":
                    description.append(" <amount> - clear an amount of messages from current channel");
                    break;
            }

            description.append("\n");
        }

        msg.getChannel().block().createEmbed(embedCreateSpec -> {
            User user = msg.getAuthor().orElse(msg.getClient().getSelf().block());
            embedCreateSpec.setFooter(user.getUsername(), user.getAvatarUrl())
                    .setDescription(description.toString())
                    .setColor(Color.LIGHT_SEA_GREEN)
                    .setTitle(title);
        }).subscribe();
    }
}
