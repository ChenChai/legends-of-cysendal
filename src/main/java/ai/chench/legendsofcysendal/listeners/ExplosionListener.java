package ai.chench.legendsofcysendal.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class ExplosionListener implements Listener {

    Plugin plugin;
    public ExplosionListener(Plugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {




    }

}
