package com.bot.data;

import com.bot.entities.FeedsEntity;
import com.bot.entities.PrefixesEntity;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    private static Map<String, String> prefixes = new HashMap<>();
    private static List<String> availableCommands = Arrays.asList("prefix", "ask", "news", "help", "stack", "stack_news", "clear");
    private static Map<String, String> availableNewsFeeds = new HashMap<>();

    public static String getPrefix(String id) {
        if (prefixes.containsKey(id)) {
            return prefixes.get(id);
        }

        EntityManager em = ManagerEntity.getEm().createEntityManager();
        em.getTransaction().begin();

        PrefixesEntity prefix = em.find(PrefixesEntity.class, id);

        if (prefix == null) {
            prefix = new PrefixesEntity();
            prefix.setId(id);
            prefix.setPrefix("#");
            em.persist(prefix);
        }

        em.getTransaction().commit();

        em.close();
        prefixes.put(id, prefix.getPrefix());
        return prefix.getPrefix();
    }

    public static void setPrefix(String id, String prefix) {
        EntityManager em = ManagerEntity.getEm().createEntityManager();
        em.getTransaction().begin();
        PrefixesEntity p = em.find(PrefixesEntity.class, id);

        if (p == null) {
            p = new PrefixesEntity();
            p.setId(id);
        }
        p.setPrefix(prefix);

        em.persist(p);

        em.getTransaction().commit();
        prefixes.put(id, prefix);

        em.close();
    }

    public static List<String> getAvailableCommands() {
        return availableCommands;
    }

    public static Map<String, String> getAvailableNewsFeeds() {
        if (availableNewsFeeds.size() == 0) {
            EntityManager em = ManagerEntity.getEm().createEntityManager();

            List<FeedsEntity> feeds = em.createNativeQuery("SELECT * FROM feeds", FeedsEntity.class).getResultList();
            feeds.forEach(feedsEntity -> {
                availableNewsFeeds.put(feedsEntity.getName(), feedsEntity.getFeedUrl());
            });

            em.close();
        }
        return availableNewsFeeds;
    }
}
