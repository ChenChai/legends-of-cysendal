package ai.chench.legendsofcysendal.listeners;

import ai.chench.legendsofcysendal.util.DamageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

public class ExplosionListener implements Listener {

    private Plugin plugin;
    private Player explosionOwner = null;
    private float maxDamage;

    public ExplosionListener(Plugin plugin) { this.plugin = plugin; }

    // returns false if explosion was cancelled, true otherwise.
    // creates an explosion that effectively has an owner, in the world the owner is in.
    // maxDamage is the maximum amount of damage an explosion can do.
    public boolean createExplosion(Player explosionOwner, Location loc, float power, boolean setFire, float maxDamage) {
        this.explosionOwner = explosionOwner;
        this.maxDamage = maxDamage;
        // attempt to create an explosion.
        // Before this returns, the onEntityDamage listeners will be called,
        // relying on the fact MINECRAFT IS SINGLE THREADED! Would not necessarily work otherwise.
        boolean success = explosionOwner.getWorld().createExplosion(loc, power, setFire);

        this.explosionOwner = null;
        this.maxDamage = Float.MAX_VALUE;
        return success;
    }


    // this listener listens for explosion damage caused by itself, finding a player responsible for the damage.
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // we only want to listen for living entities getting damaged, not things like items.
        if (!(event.getEntity() instanceof Damageable)) { return; }

        Damageable entity = (Damageable) event.getEntity();
        // we only want to listen for damage caused by explosion, return otherwise.
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION && event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            return;
        }

        // if this is null, that means that this class was not responsible for that explosion.
        if (this.explosionOwner == null) {
            return;
        }

        // lower max damage an entity can take to
        event.setDamage(Math.min(event.getDamage(), maxDamage));

        Player owner = explosionOwner;

        // add damage as player's damage contribution.
        DamageManager damageManager = new DamageManager(plugin);
        damageManager.addDamageContribution(explosionOwner, entity, event.getDamage());

    }
}
