package ai.chench.legendsofcysendal.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

// This listener handles the death of mobs, and awards soul points accordingly.
public class KillListener implements Listener {
    private Plugin plugin;
    public KillListener(Plugin plugin){ this.plugin = plugin; }

    // register players as damage contributors when they damage entities
    // count up the total damage they have done.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Damageable entity = (Damageable) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Bukkit.broadcastMessage(entity.getName() + " took " + event.getFinalDamage());
            double damageContribution = event.getFinalDamage();

            String uniqueId = player.getUniqueId().toString();
            String metadataKey = uniqueId + "damageContribution";

            // limit this hit's max damage to the entity's current health
            damageContribution = (damageContribution > entity.getHealth() ? entity.getHealth() : damageContribution);

            // add their previous damage contribution to their current
            if (entity.hasMetadata(metadataKey)) {
                double previousDamageContribution = entity.getMetadata(metadataKey).get(0).asDouble();
                damageContribution += previousDamageContribution;
            }

            // update the metadata
            entity.removeMetadata(metadataKey, plugin);
            entity.setMetadata(metadataKey, new FixedMetadataValue(plugin, damageContribution));
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                if (entity.hasMetadata(player.getUniqueId().toString() + "damageContribution")) {
                    player.sendMessage("You did " +
                            String.format("%.1f" ,entity.getMetadata(player.getUniqueId().toString() + "damageContribution").get(0).asDouble())
                            + " damage to " + entity.getName());
                }
            }
        }


    }

}
