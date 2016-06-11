package cc.isotopestudio.Phoenix;

import com.earth2me.essentials.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mars on 6/11/2016.
 * Copyright ISOTOPE Studio
 */
public class Phoenix extends JavaPlugin implements Listener {
    private static final String pluginName = "Phoenix";
    private static Map<Player, Location> death = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(pluginName + "成功加载!");
        getLogger().info(pluginName + "由ISOTOPE Studio制作!");
        getLogger().info("http://isotopestudio.cc");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        death.put(event.getEntity(), event.getEntity().getLocation());
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Location location = death.get(event.getPlayer());
        if (location == null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                Location safe = null;
                try {
                    safe = LocationUtil.getSafeDestination(location);
                } catch (Exception ignored) {
                    safe = location;
                } finally {
                    event.getPlayer().teleport(safe);
                    event.getPlayer().sendMessage("§c§l浴火重生");
                }
            }
        }.runTaskLater(this, 1);
    }
}
