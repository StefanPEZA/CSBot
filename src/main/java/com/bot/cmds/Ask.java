package com.bot.cmds;

import com.bot.data.Secrets;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.Color;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.*;

public class Ask implements Command {
    @Override
    public void execute(Object... args) {
        Message msg = (Message) args[0];
        String query = (String) args[1];
        query = query.replaceAll("\\+", "plus");

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://api.wolframalpha.com/v2/query")
                .queryParam("appid", Secrets.wolphramAppId())
                .queryParam("format", "plaintext")
                .queryParam("output", "json")
                .queryParam("excludepodid", "Input")
                .queryParam("input", query.trim());

        System.out.println("Sending request to: " + builder.build().toUri());

        sendRequest(builder.build().toUri(), msg);
    }

    private void sendRequest(URI uri, Message msg) {
        MessageChannel channel = msg.getChannel().block();

        Message mes = Objects.requireNonNull(channel).createMessage("Processing...").block();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
        restTemplate.getMessageConverters().add(0, converter);

        HttpHeaders headers = new HttpHeaders();

        HttpEntity<?> request = new HttpEntity<>(headers);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> queryResult;
        response.put("default", "Sorry, i don't know the answer");

        ResponseEntity<Object> responseAsk = ResponseEntity.ok(response);

        try {
            responseAsk = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    Object.class
            );
        } catch (HttpClientErrorException e) {
            System.out.println(e.getMessage());
        }

        response = (Map<String, Object>) responseAsk.getBody();
        queryResult = (Map<String, Object>) Objects.requireNonNull(response).get("queryresult");


        if (response.get("default") != null || queryResult.get("success").equals(false)) {
            channel.createEmbed(embedCreateSpec -> {
                User user = msg.getAuthor().orElse(msg.getClient().getSelf().block());
                embedCreateSpec.setFooter(Objects.requireNonNull(user).getUsername(), user.getAvatarUrl())
                        .setDescription("Sorry, i don't know the answer!")
                        .setColor(Color.LIGHT_SEA_GREEN)
                        .setTitle("Sorry!")
                        .setTimestamp(Instant.now());
            }).subscribe();
            Objects.requireNonNull(mes).delete().subscribe();
            return;
        }

        channel.createEmbed(embedCreateSpec -> {
            User user = msg.getAuthor().orElse(msg.getClient().getSelf().block());

            List<Map<String, Object>> pods = (List<Map<String, Object>>) queryResult.get("pods");

            for (int i = 0; i < Math.min(6, pods.size()); i++) {
                List<Map<String, Object>> subpods = (List<Map<String, Object>>) pods.get(i).get("subpods");

                String title = (String) pods.get(i).get("title");
                StringBuilder plaintext = new StringBuilder();
                for (int j = 0; j < subpods.size(); j++) {
                    String temp = (String) subpods.get(j).get("plaintext");
                    if (!temp.trim().equals("")) {
                        plaintext.append(temp);
                        plaintext.append("\n");
                    }
                }

                if (!plaintext.toString().trim().equals("")) {
                    embedCreateSpec.addField(title, plaintext.toString(), false);
                }
            }
            embedCreateSpec.setFooter(Objects.requireNonNull(user).getUsername(), user.getAvatarUrl())
                    .setColor(Color.LIGHT_SEA_GREEN)
                    .setTimestamp(Instant.now());
        }).subscribe();

        Objects.requireNonNull(mes).delete().subscribe();
    }
}
