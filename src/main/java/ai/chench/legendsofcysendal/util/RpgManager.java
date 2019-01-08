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

    // compares player's soul points to level thresholds, and changes player's level accordingly
    // returns true if level was updated, false otherwise.
    public boolean updateLevel(Player player) {


        boolean updated = false;

        int points = getSoulPoints(player);

        // if player is new player, set their level to 1.
        if (!plugin.getConfig().isInt("players." + player.getUniqueId() + ".level")) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".level", 1);
            updated = true;
        }

        int playerLevel = plugin.getConfig().getInt("players." + player.getUniqueId() + ".level");
        int correctLevel = 0;

        // TODO: swap to binary search\
        // finds correct level based on points, from the level list in config file.
        List<Integer> levelList = plugin.getConfig().getIntegerList("level");
        while (correctLevel < levelList.size() && levelList.get(correctLevel) <= points) {
            correctLevel++;
        }

        if (correctLevel != playerLevel) {
            plugin.getConfig().set("players." + player.getUniqueId() + ".level", correctLevel);
            updated = true;
        }

        return updated;
    }
    public RpgClass getClass(Player player) {
        return RpgClass.valueOf(plugin.getConfig().getString("players." + player.getUniqueId() + ".class"));
    }
    public void setClass(Player player, RpgClass rpgClass) {
        plugin.getConfig().set("players." + player.getUniqueId() + ".class", rpgClass.toString());
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

    // returns the minimum number of soul points needed to be a given level, or -1 if level is out of bounds.
    public int getSoulPointsForLevel(int level) {
        List<Integer> levelList = plugin.getConfig().getIntegerList("level");

        if (level > 0 && level < levelList.size()) {
            return levelList.get(level);
        } else {
            return -1;
        }

    }

    public int getMaxLevel() {
        return plugin.getConfig().getIntegerList("level").size();
    }

}
