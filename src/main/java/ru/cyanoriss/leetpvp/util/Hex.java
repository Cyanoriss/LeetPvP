package ru.cyanoriss.leetpvp.util;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Hex {

    public static String color(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {

            String hexCode = text.substring(matcher.start(), matcher.end());
            String replaceSharp = hexCode.replace('#', 'x');
            StringBuilder builder = new StringBuilder();

            replaceSharp.chars().forEach(c -> builder.append("&").append((char) c));

            text = text.replace(hexCode, builder.toString());
            matcher = pattern.matcher(text);

        }

        return ChatColor.translateAlternateColorCodes('&', text).replace("&", "");

    }

    public static List<String> color(List<String> text) {
        return text.stream().map(Hex::color).collect(Collectors.toList());
    }
}