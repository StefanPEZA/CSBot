package com.bot.cmds;

import com.bot.data.Data;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Stack implements Command {
    @Override
    public void execute(Object... args) {
        Message msg = (Message) args[0];

        String title = (String) args[1];
        String tag = (String) args[2];

        String prefix = Data.getPrefix(msg.getGuildId().orElse(Snowflake.of("1")).asString());

        if (tag.trim().equals("") && title.trim().equals("")) {
            msg.getChannel().block()
                    .createMessage("Usage: ```" + prefix + "stack [title] #[tag]```")
                    .block();
            return;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://api.stackexchange.com/2.2/search")
                // Add query parameter
                .queryParam("site", "stackoverflow")
                .queryParam("tagged", tag)
                .queryParam("pagesize", "3")
                .queryParam("order", "desc")
                .queryParam("sort", "relevance")
                .queryParam("intitle", title);

        System.out.println("Sending request to: " + builder.build().toUri());

        sendRequest(builder.build().toUri(), msg);
    }

    private void sendRequest(URI uri, Message msg) {
        MessageChannel channel = msg.getChannel().block();

        Message mes = channel.createMessage("Processing...").block();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> request = new HttpEntity<>(headers);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("default", "No results found!");

        ResponseEntity<Object> responseEntity = ResponseEntity.ok(response);

        try {
            responseEntity = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    Object.class
            );
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }

        response = (Map<String, Object>) responseEntity.getBody();

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        if (items.size() > 0) {
            for (Map<String, Object> item : items) {
                channel.createEmbed(embedCreateSpec -> {
                    String link = (String) item.get("link");
                    Integer id = (Integer) item.get("accepted_answer_id");
                    User user = msg.getAuthor().orElse(msg.getClient().getSelf().block());

                    embedCreateSpec.setFooter(user.getUsername(), user.getAvatarUrl()).setUrl(link);

                    if (id != null) {
                        embedCreateSpec.setDescription("Accepted answer: " + link + "/#" + id);
                    } else {
                        embedCreateSpec.setDescription("No accepted answer: " + link);
                    }

                    embedCreateSpec.addField("Tags", item.get("tags").toString(), false)
                            .setColor(Color.LIGHT_SEA_GREEN)
                            .setTitle(Jsoup.parse((String) item.get("title")).wholeText())
                            .setTimestamp(Instant.now());
                }).subscribe();
            }
        } else {
            channel.createEmbed(embedCreateSpec -> {
                User user = msg.getAuthor().orElse(msg.getClient().getSelf().block());

                embedCreateSpec.setFooter(user.getUsername(), user.getAvatarUrl())
                        .setDescription("No results found")
                        .setColor(Color.LIGHT_SEA_GREEN)
                        .setTitle("Sorry!")
                        .setTimestamp(Instant.now());
            }).subscribe();
        }

        mes.delete().block();
    }
}
