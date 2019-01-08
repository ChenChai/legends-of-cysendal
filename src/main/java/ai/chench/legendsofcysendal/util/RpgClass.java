package ai.chench.legendsofcysendal.util;

import org.bukkit.plugin.Plugin;

public enum RpgClass {
    FIGHTER,
    MAGE,
    RANGER,
    NONE;

    public String getDisplayName(Plugin plugin) {
        if (plugin.getConfig().isString("lore.classSelect." + this.toString() + ".displayName"))
            return plugin.getConfig().getString("lore.classSelect." + this.toString() + ".displayName");

        return this.toString(); // default
    }
}
