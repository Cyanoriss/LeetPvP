package ru.cyanoriss.leetpvp;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ExitChecker implements Listener {
    private final Main plugin;

    public ExitChecker(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.player.equals(event.getPlayer().getName())) {
            plugin.player = null;
        }
    }
}
