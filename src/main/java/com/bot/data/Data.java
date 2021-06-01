package com.bot.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    private static Map<String, String> prefixes = new HashMap<>();
    private static List<String> availableCommands = Arrays.asList("prefix", "ask", "news", "help", "stack", "stack_news", "clear");
    private static Map<String, String> availableNewsFeeds = new HashMap<>();

    public static String getPrefix(String id) {
        if (prefixes.getOrDefault(id, "-1").equals("-1")) {
            setPrefix(id, "#");
        }
        return prefixes.get(id);
    }

    public static void setPrefix(String id, String prefix) {
        prefixes.put(id, prefix);
    }

    public static List<String> getAvailableCommands() {
        return availableCommands;
    }

    public static Map<String, String> getAvailableNewsFeeds() {
        return availableNewsFeeds;
    }
}
