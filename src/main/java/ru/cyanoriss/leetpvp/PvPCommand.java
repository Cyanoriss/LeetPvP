package ru.cyanoriss.leetpvp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.cyanoriss.leetpvp.util.Hex;
import ru.cyanoriss.leetpvp.util.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PvPCommand implements TabExecutor {
    private final Main plugin;

    public PvPCommand(Main plugin) {
        this.plugin = plugin;
    }

    private void sendUsage(CommandSender sender) {
        for (String line : plugin.getConfig().getStringList("usage")) {
            sender.sendMessage(Hex.color(line));
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("leetpvp.admin")) {
                plugin.reloadConfig();
                sender.sendMessage(Hex.color("&aКонфиг перезагружен"));
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Hex.color("&cЭта команда только для игроков"));
            return true;
        }
        Player player = (Player) sender;

        if (args[0].equalsIgnoreCase("search")
                || plugin.getConfig().getStringList("search-aliases").contains(args[0].toLowerCase())) {
            if (plugin.player == null) {
                if (!player.hasPermission("leetpvp.bypass") && plugin.cooldowns.getOrDefault(player.getName(), 0L) > System.currentTimeMillis()) {
                    long cooldown = (plugin.cooldowns.get(player.getName()) - System.currentTimeMillis()) / 1000 + 1;
                    player.sendMessage(Hex.color(
                            plugin.getConfig().getString("messages.cooldown")
                                    .replace("{time}", String.valueOf(cooldown))
                    ));
                    return true;
                }

                plugin.player = player.getName();
                plugin.cooldowns.put(player.getName(), System.currentTimeMillis() + plugin.getConfig().getInt("cooldown") * 1000L);
                player.sendMessage(Hex.color(plugin.getConfig().getString("messages.search")));

                Component text = Component.text(Hex.color(
                                plugin.getConfig().getString("messages.alert")
                                        .replace("{player}", player.getName()))
                        )
                        .clickEvent(ClickEvent.runCommand("/pvp search"));

                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getName().equals(player.getName())) continue;
                    pl.sendMessage(text);
                }
                return true;
            }

            if (plugin.player.equals(player.getName())) {
                player.sendMessage(Hex.color(plugin.getConfig().getString("messages.already")));
                return true;
            }

            Player pl1 = Bukkit.getPlayer(plugin.player);
            plugin.player = null;
            Player pl2 = player;

            Location center;
            int side;

            World world = Bukkit.getWorld(plugin.getConfig().getString("rtp.world"));
            if (plugin.getConfig().getBoolean("rtp.worldborder")) {
                center = world.getWorldBorder().getCenter();
                side = (int) world.getWorldBorder().getSize();
            } else {
                String[] xz = plugin.getConfig().getString("rtp.center").split(",");
                int x = Integer.parseInt(xz[0]);
                int z = Integer.parseInt(xz[1]);
                center = new Location(world, x, 0, z);
                side = plugin.getConfig().getInt("rtp.side");
            }
            Location loc1 = null;

            for (int i = 0; i < plugin.getConfig().getInt("rtp.attempts"); i++) {
                Location loc = Rand.location(center, side);
                if (!plugin.getConfig().getStringList("rtp.forbidden-blocks").contains(loc.getBlock().getType().name())) {
                    loc1 = loc.add(0, 1, 0);
                    break;
                }
            }

            if (loc1 == null) {
                pl1.sendMessage(Hex.color(plugin.getConfig().getString("messages.failed")));
                pl2.sendMessage(Hex.color(plugin.getConfig().getString("messages.failed")));
                return true;
            }

            Location loc2 = null;

            String[] xz = plugin.getConfig().getString("rtp.distance").split("-");
            for (int i = 0; i < plugin.getConfig().getInt("rtp.attempts"); i++) {
                Location loc = Rand.location(loc1, Integer.parseInt(xz[0]), Integer.parseInt(xz[1]));
                if (!plugin.getConfig().getStringList("rtp.forbidden-blocks").contains(loc.getBlock().getType().name())) {
                    loc2 = loc.add(0, 1, 0);
                    break;
                }
            }

            if (loc2 == null) {
                pl1.sendMessage(Hex.color(plugin.getConfig().getString("messages.failed")));
                pl2.sendMessage(Hex.color(plugin.getConfig().getString("messages.failed")));
                return true;
            }

            loc1.setDirection(loc2.toVector().subtract(loc1.toVector()));
            loc2.setDirection(loc1.toVector().subtract(loc2.toVector()));

            pl1.teleport(loc1);
            pl2.teleport(loc2);

            if (!plugin.getConfig().getStringList("effects").isEmpty()) {
                for (String line : plugin.getConfig().getStringList("effects")) {
                    String[] build = line.split(";");

                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(build[0]), Integer.parseInt(build[2]) * 20, Integer.parseInt(build[1]));
                    pl1.addPotionEffect(effect);
                    pl2.addPotionEffect(effect);
                }
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("cancel")
                || plugin.getConfig().getStringList("cancel-aliases").contains(args[0].toLowerCase())) {
            if (plugin.player != null && plugin.player.equals(player.getName())) {
                plugin.player = null;
                player.sendMessage(Hex.color(plugin.getConfig().getString("messages.cancel")));
                return true;
            }

            player.sendMessage(Hex.color(plugin.getConfig().getString("messages.cancel-error")));
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>(List.of("search", "cancel"));
        completions.addAll(plugin.getConfig().getStringList("search-aliases"));
        completions.addAll(plugin.getConfig().getStringList("cancel-aliases"));
        if (sender.hasPermission("leetpvp.admin")) {
            completions.add("reload");
        }

        if (args.length == 1) {
            return filter(completions, args[0]);
        }
        return List.of();
    }

    private List<String> filter(@Nullable List<String> list, String start) {
        return Optional.ofNullable(list)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(element -> element.startsWith(start))
                .collect(Collectors.toList());
    }
}
