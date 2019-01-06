package ai.chench.legendsofcysendal.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;

// this class allows easier access and modification of players' soul points, level, etc.
// PREREQ: Player objects passed in must have valid UUID.
public class RpgManager {
    Plugin plugin;

    public RpgManager(Plugin plugin) {
        this.plugin = plugin;
    }

    // returns the amount of soul points a player has
    public int getSoulPoints(Player player) {
        // check if the value is valid in the config file; if not, then default to zero.
        if (!plugin.getConfig().isInt("players." + player.getUniqueId() + ".sp")) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".sp", 0);

            /*
            plugin.getLogger().severe("Player " + player.getDisplayName() + " " +
                    "has invalid value for soul points in config.yml!");
            return -1;
            */
        }
        return plugin.getConfig().getInt("players." + player.getUniqueId() + ".sp");
    }

    // sets the number of soul points a player has
    public void setSoulPoints(Player player, int points) {
        plugin.getConfig().set("players." + player.getUniqueId() + ".sp", points);
        plugin.saveConfig();
    }

    // adds a number of soul points to the total a player has. Can be negative.
    public void addSoulPoints(Player player, int points) {
        plugin.getConfig().set("players." + player.getUniqueId() + ".sp", points + getSoulPoints(player));
        plugin.saveConfig();
    }

    public int getLevel(Player player) {
        if (!plugin.getConfig().isInt("players." + player.getUniqueId() + ".level")) {
            plugin.getLogger().severe("Player " + player.getDisplayName() + " " +
                    "has invalid value for level in config.yml!");
            return -1;
        }
        return plugin.getConfig().getInt("players." + player.getUniqueId() + ".level");
    }

    // TODO: test if this function works.
    // compares player's soul points to level thresholds, and changes player's level accordingly
    // returns true if level was updated, false otherwise.
    public boolean updateLevel(Player player) {
        List<Integer> levelList = plugin.getConfig().getIntegerList("level");

        boolean updated = false;
        int level = 1;
        int points = getSoulPoints(player);

        // check if player is new player.
        if (!plugin.getConfig().isInt("player." + player.getUniqueId() + ".level")) {
            plugin.getConfig().set("player." + player.getUniqueId() + ".level", 1);
            updated = true;
        } else {
            level = plugin.getConfig().getInt("player." + player.getUniqueId() + ".level");
        }

        // check if player is at correct level or at max level
        if ((points >= levelList.get(level) && points < levelList.get(level + 1))
                || points >= levelList.get(levelList.size() - 1)) {
            return updated;
        }

        // bring level up to appropriate points.
        // level will undershoot by 1, which is fine, since arrays start at zero.
        updated = true;
        while(levelList.get(level) <= points) {
            level++;
        }
        plugin.getConfig().set("players." + player.getUniqueId() + ".level", level);
        plugin.saveConfig();
        return updated;
    }
    public String getClass(Player player) {
        return plugin.getConfig().getString("players." + player.getUniqueId() + ".class");
    }
    public void setClass(Player player, String className) {
        plugin.getConfig().set("players." + player.getUniqueId() + ".class", className);
        plugin.saveConfig();
    }

    public boolean isFirstJoin(Player player) {
        if (!plugin.getConfig().isBoolean("players." + player.getUniqueId() + ".firstJoin")) {
            plugin.getLogger().severe("Player " + player.getDisplayName() + " " +
                    "has invalid value for firstJoin in config.yml!");
            return false;
        }

        return plugin.getConfig().getBoolean("players." + player.getUniqueId() + ".firstJoin");
    }

    public void setFirstJoin(Player player, boolean firstJoin) {
        plugin.getConfig().set("players." + player.getUniqueId() + ".firstJoin", firstJoin);
        plugin.saveConfig();
    }

}
