package ai.chench.legendsofcysendal.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class ExplosionListener implements Listener {

    private Plugin plugin;
    private Player explosionOwner = null;

    public ExplosionListener(Plugin plugin) { this.plugin = plugin; }

    // returns false if explosion was cancelled, true otherwise.
    // creates an explosion that effectively has an owner, in the world the owner is in.
    public boolean createExplosion(Player explosionOwner, Location loc, float power, boolean setFire) {
        this.explosionOwner = explosionOwner;

        // attempt to create an explosion.
        // Before this returns, the onEntityDamage listeners will be called,
        // relying on the fact MINECRAFT IS SINGLE THREADED! Would not necessarily work otherwise.
        boolean success = explosionOwner.getWorld().createExplosion(loc, power, setFire);

        this.explosionOwner = null;
        return success;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // we only want to listen for damage caused by explosion, return otherwise.
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            return;
        }

        // if this is null, that means that this class was not responsible for that explosion.
        if (this.explosionOwner == null) {
            return;
        }

        Player owner = explosionOwner;
        Bukkit.broadcastMessage(owner.getDisplayName() + " did " + event.getDamage() + " explosive damage to " + event.getEntity().getName());
    }

}
