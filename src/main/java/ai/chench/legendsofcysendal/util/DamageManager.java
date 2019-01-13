package ai.chench.legendsofcysendal.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class DamageManager {
    private Plugin plugin;
    public DamageManager(Plugin plugin) { this.plugin = plugin; }

    // adds damage contribution to an entity's metadata
    public void addDamageContribution(Player player, Damageable damagedEntity, double damageContribution) {

        String uniqueId = player.getUniqueId().toString();
        String metadataKey = uniqueId + "damageContribution";

        // limit the hit's max damage to the entity's current health
        damageContribution = (damageContribution > damagedEntity.getHealth() ? damagedEntity.getHealth() : damageContribution);

        // add their previous damage contribution to their current
        if (damagedEntity.hasMetadata(metadataKey)) {
            double previousDamageContribution = damagedEntity.getMetadata(metadataKey).get(0).asDouble();
            damageContribution += previousDamageContribution;
        }

        // update the metadata
        damagedEntity.removeMetadata(metadataKey, plugin);
        damagedEntity.setMetadata(metadataKey, new FixedMetadataValue(plugin, damageContribution));

    }

    // gets how much damage a player did to an entity.
    public double getDamageContribution(Player player, Entity entity) {
        String uniqueId = player.getUniqueId().toString();
        String metadataKey = uniqueId + "damageContribution";

        if (entity.hasMetadata(metadataKey)) {
            return entity.getMetadata(metadataKey).get(0).asDouble();
        }

        return 0.0;
    }

}
