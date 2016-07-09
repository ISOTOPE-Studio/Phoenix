package cc.isotopestudio.Phoenix;

import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mars on 6/11/2016.
 * Copyright ISOTOPE Studio
 */
public class Phoenix extends JavaPlugin implements Listener, CommandExecutor {
    private static final String pluginName = "Phoenix";
    private static final Map<Player, Location> death = new HashMap<>();
    private static int radius;

    @Override
    public void onEnable() {

        File file;
        file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveDefaultConfig();
        }
        radius = getConfig().getInt("radius", 0);

        this.getCommand("reborn").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(pluginName + "成功加载!");
        getLogger().info(pluginName + "由ISOTOPE Studio制作!");
        getLogger().info("http://isotopestudio.cc");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        death.put(event.getEntity(), event.getEntity().getLocation());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTp(PlayerTeleportEvent event) {
        if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER
                && event.getTo().getBlockY() >= 128) {
            event.setCancelled(true);
            final Player player = event.getPlayer();
            final Location to = event.getFrom().clone();
            final Location from = event.getFrom().clone();
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc;
                    try {
                        loc = getSafe(LocationUtil.getSafeDestination(from));
                    } catch (Exception e) {
                        loc = to.add(0, -2, 0);
                        loc.getBlock().setType(Material.AIR);
                        loc.add(0, -1, 0).getBlock().setType(Material.AIR);
                    }
                    player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    if (loc.getBlock().getType() != Material.AIR) {
                        loc.getBlock().setType(Material.AIR);
                        loc.add(0, 1, 0).getBlock().setType(Material.AIR);
                    }
                }
            }.runTaskLater(this, 5);
            System.out.print(event.getTo().toString() + " " + event.getCause());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Location location = death.get(event.getPlayer());
        if (location == null) return;
        //location.add(Math.random() * radius / 2, 0, Math.random() * radius / 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                Location safe = null;
                try {
                    safe = getSafe(LocationUtil.getSafeDestination(location));
                } catch (Exception ignored) {
                    safe = location;
                } finally {
                    event.getPlayer().teleport(safe);
                    event.getPlayer().sendMessage("§c§l浴火重生");
                }
            }
        }.runTaskLater(this, 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                Location safe = event.getPlayer().getLocation();
                if (safe.getWorld().getEnvironment() == World.Environment.NETHER && safe.getBlockY() >= 128) {
                    try {
                        safe = getSafe(LocationUtil.getSafeDestination(location));
                    } catch (Exception ignored) {
                        safe = location;
                    } finally {
                        event.getPlayer().teleport(safe);
                        if (safe.getBlock().getType() != Material.AIR) {
                            safe.getBlock().setType(Material.AIR);
                            safe.add(0, 1, 0).getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }.runTaskLater(this, 40);
    }

    private Location getSafe(Location safe) {
        if (safe.getWorld().getEnvironment() == World.Environment.NETHER && safe.getBlockY() >= 128) {
            safe = safe.add(0, -20, 0);
            int y = safe.getBlockY();
            while (safe.getBlockY() >= 126) {
                try {
                    safe = LocationUtil.getSafeDestination(safe.add(Math.random() * 5, y - 5, Math.random() * 5));
                } catch (Exception e) {
                    safe = safe.add(0, -2, 0);
                    safe.getBlock().setType(Material.AIR);
                    safe.add(0, -1, 0).getBlock().setType(Material.AIR);
                }
            }
        }
        return safe;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("reborn")) {
            /*
            if (!sender.hasPermission("binding")) {
                sender.sendMessage("你没有权限");
                return true;
            }
            */
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c必须要玩家才能执行");
                return true;
            }
            Player player = (Player) sender;
            try {
                player.teleport(getSafe(LocationUtil.getSafeDestination((player.getLocation().
                        add(Math.random() * radius / 2, 0, Math.random() * radius / 2)))));
            } catch (Exception e) {
                player.sendMessage("§c失败 请再试一次");
                return true;
            }
            player.sendMessage("§a随机传送");
            return true;
        }
        return false;
    }
}
