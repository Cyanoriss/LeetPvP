package ru.cyanoriss.leetpvp;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {
    public String player;
    public final Map<String, Long> cooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("pvp").setExecutor(new PvPCommand(this));
        getServer().getPluginManager().registerEvents(new ExitChecker(this), this);
    }
}