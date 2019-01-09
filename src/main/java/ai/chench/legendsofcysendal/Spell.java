package ai.chench.legendsofcysendal;

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
    };

    public abstract boolean makeEffect(Player player, Plugin plugin);
}
