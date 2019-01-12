package ai.chench.legendsofcysendal;

import ai.chench.legendsofcysendal.listeners.ExplosionListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum Spell {
    LIGHTNING_BOLT {
        @Override
        public boolean makeEffect(Player player, Plugin plugin) {
            player.sendMessage("Boom.");
            return true;
        }
    },
    FLEET_OF_FOOT {
        @Override
        public boolean makeEffect(Player player, Plugin plugin) {
            int duration = plugin.getConfig().getInt("spells." + this.toString() + ".duration");
            int amplifier = plugin.getConfig().getInt("spells." + this.toString() + ".amplifier");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier), true);
            return true;
        }
    },
    EXPLODE {
        @Override
        public boolean makeEffect(Player player, Plugin plugin) {
            ExplosionListener explosionListener = Main.explosionListener;
            return explosionListener.createExplosion(player, player.getLocation(), 4.0f, false, 6.0f);
        }
    }
    ;


    public abstract boolean makeEffect(Player player, Plugin plugin);

    // returns display name of a spell from the configuration file.
    public String getDisplayName(Plugin plugin) {
        return plugin.getConfig().getString("spells." + this.toString() + ".displayName");
    }

    public int getCoolDown(Plugin plugin) {
        return plugin.getConfig().getInt("spells." + this.toString() + ".cooldown");
    }

    public int getLevel(Plugin plugin) {
        return plugin.getConfig().getInt("spells." + this.toString() + ".level");
    }
}
