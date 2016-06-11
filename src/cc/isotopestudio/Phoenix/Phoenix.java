package cc.isotopestudio.Phoenix;

import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mars on 6/11/2016.
 * Copyright ISOTOPE Studio
 */
public class Phoenix extends JavaPlugin implements Listener {
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
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(pluginName + "成功加载!");
        getLogger().info(pluginName + "由ISOTOPE Studio制作!");
        getLogger().info("http://isotopestudio.cc");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        death.put(event.getEntity(), event.getEntity().getLocation());
    }
/*
    @EventHandler
    public void onPlayerRespawn(PlayerTeleportEvent event) {
        for (RegisteredListener listener : event.getHandlers().getRegisteredListeners())
            System.out.print(listener.getPlugin().getName());
        System.out.print(event.getTo().toString() + " " + event.getCause());
    }
*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Location location = death.get(event.getPlayer());
        if (location == null) return;
        location.add(Math.random() * radius / 2, 0, Math.random() * radius / 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                Location safe = null;
                try {
                    safe = LocationUtil.getSafeDestination(location);
                    if (safe.getWorld().getEnvironment() == World.Environment.NETHER && safe.getBlockY() >= 128) {
                        safe = safe.add(0, -20, 0);
                        int y = safe.getBlockY();
                        while (safe.getBlockY() >= 128) {
                            safe = LocationUtil.getSafeDestination(safe.add(Math.random() * 5, y - 5, Math.random() * 5));
                        }
                    }
                } catch (Exception ignored) {
                    safe = location;
                } finally {
                    event.getPlayer().teleport(safe);
                    event.getPlayer().sendMessage("§c§l浴火重生");
                }
            }
        }.runTaskLater(this, 2);
    }
}
