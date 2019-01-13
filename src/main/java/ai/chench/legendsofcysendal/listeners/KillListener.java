package ai.chench.legendsofcysendal.listeners;

import ai.chench.legendsofcysendal.util.DamageManager;
import ai.chench.legendsofcysendal.util.RpgManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

// This listener handles the death of mobs, and awards soul points accordingly.
public class KillListener implements Listener {
    private Plugin plugin;
    public KillListener(Plugin plugin){ this.plugin = plugin; }

    // register players as damage contributors when they damage entities
    // count up the total damage they have done.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof  Damageable)) { return; }
        Damageable entity = (Damageable) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Bukkit.broadcastMessage(entity.getName() + " took " + event.getFinalDamage());
            DamageManager damageManager = new DamageManager(plugin);
            damageManager.addDamageContribution(player, entity, event.getFinalDamage());
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        for (World world : Bukkit.getWorlds()) {
            for (Player player : world.getPlayers()) {
                DamageManager damageManager = new DamageManager(plugin);
                double damageContribution = damageManager.getDamageContribution(player, entity);

                if (damageContribution > 0) {
                    player.sendMessage("You did " + String.format("%.1f" ,damageContribution) + " damage to " + entity.getName());

                    // award soul points for kill
                    RpgManager rpgManager = new RpgManager(plugin);
                    int points = rpgManager.getEntitySoulPointValue(entity);
                    rpgManager.addSoulPoints(player, points);

                    // display message telling player how many points they gained
                    player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "+" + points + ChatColor.RESET + "" + ChatColor.DARK_GRAY + " Soul Points ["
                            + (entity.getCustomName() == null ? entity.getName() : entity.getCustomName()) // display custom name if it exists; normal name otherwise
                            + "]");
                    rpgManager.updateLevel(player);
                }
            }
        }
    }

}
