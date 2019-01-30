package ai.chench.legendsofcysendal.listeners;

import ai.chench.legendsofcysendal.Main;
import ai.chench.legendsofcysendal.util.DamageManager;
import ai.chench.legendsofcysendal.util.PartyManager;
import ai.chench.legendsofcysendal.util.RpgClass;
import ai.chench.legendsofcysendal.util.RpgManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.data.type.Bed;
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
    private Main plugin;
    public KillListener(Main plugin){ this.plugin = plugin; }

    // register players as damage contributors when they damage entities
    // count up the total damage they have done.
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof  Damageable)) { return; }
        Damageable entity = (Damageable) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player damagingPlayer = (Player) event.getDamager();

            // if the damage was caused by a player attacking another player, check if the players are in the same party.
            if (event.getEntity() instanceof Player) {
                Player damagedPlayer = (Player) event.getEntity();
                PartyManager partyManager = new PartyManager(plugin);

                // if players are in the same party, cancel the damage.
                if (!(partyManager.getParty(damagingPlayer) == null || partyManager.getParty(damagedPlayer) == null) &&
                partyManager.getParty(damagingPlayer).equals(partyManager.getParty(damagedPlayer))) {
                    event.setCancelled(true);
                }
            }

            Bukkit.broadcastMessage(entity.getName() + " took " + event.getFinalDamage());
            DamageManager damageManager = new DamageManager(plugin);
            damageManager.addDamageContribution(damagingPlayer, entity, event.getFinalDamage());
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

                    // award soul points for kill only if player has a class

                    RpgManager rpgManager = new RpgManager(plugin);
                    if (rpgManager.getRpgClass(player) != RpgClass.NONE) {

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

}
