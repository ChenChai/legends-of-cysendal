package ai.chench.legendsofcysendal.util;

import ai.chench.legendsofcysendal.Main;
import ai.chench.legendsofcysendal.Spell;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

// this class allows easier access and modification of players' soul points, level, etc.
// PREREQ: Player objects passed in must have valid UUID.
public class RpgManager {
    Plugin plugin;
    Main main;

    public RpgManager(Plugin plugin) {
        this.plugin = plugin;
        if (plugin instanceof Main) {
            this.main = (Main) plugin;
        }
    }

    // returns the amount of soul points a player has
    public int getSoulPoints(Player player) {
        // check if the value is valid in the config file; if not, then default to zero.
        if (!main.getPlayerDataConfig().isInt("players." + player.getUniqueId() + ".sp")) {
            main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".sp", 0);

            /*
            plugin.getLogger().severe("Player " + player.getDisplayName() + " " +
                    "has invalid value for soul points in config.yml!");
            return -1;
            */
        }
        return main.getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".sp");
    }

    // sets the number of soul points a player has
    public void setSoulPoints(Player player, int points) {
        main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".sp", points);
        main.savePlayerDataConfig();
    }

    // adds a number of soul points to the total a player has. Can be negative.
    public void addSoulPoints(Player player, int points) {
        // player can only gain soul points if they are in a class.
        if (getRpgClass(player)!= RpgClass.NONE) {
            main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".sp", points + getSoulPoints(player));
            main.savePlayerDataConfig();
        }
    }

    public int getLevel(Player player) {
        if (!main.getPlayerDataConfig().isInt("players." + player.getUniqueId() + ".level")) {
            plugin.getLogger().severe("Player " + player.getDisplayName() + " " +
                    "has invalid value for level in config.yml!");
            return -1;
        }
        return main.getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".level");
    }

    // compares player's soul points to level thresholds, and changes player's level accordingly
    // returns true if level was updated, false otherwise. Also sends player a message if their level changed,
    // telling them they levelled up and the spells that are new to that level.
    public boolean updateLevel(Player player) {
        boolean updated = false;

        int points = getSoulPoints(player);

        // if player is new player, set their level to 1.
        if (!main.getPlayerDataConfig().isInt("players." + player.getUniqueId() + ".level")) {
            main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".level", 1);
            updated = true;
        }

        int playerLevel = main.getPlayerDataConfig().getInt("players." + player.getUniqueId() + ".level");
        int correctLevel = 0;

        // TODO: swap to binary search
        // finds correct level based on points, from the level list in config file.
        List<Integer> levelList = plugin.getConfig().getIntegerList("level");
        while (correctLevel < levelList.size() && levelList.get(correctLevel) <= points) {
            correctLevel++;
        }

        if (correctLevel != playerLevel) {
            main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".level", correctLevel);
            main.savePlayerDataConfig();
            updated = true;
        }

        if (updated) {
            player.sendMessage(String.format(plugin.getConfig().getString("messages.levelUp"), correctLevel)); // tell the player they've levelled up!
            SpellManager spellManager = new SpellManager(player, plugin);
            for (Spell spell : spellManager.getClassSpells(getRpgClass(player))) { // loop through the class's spells and see if any new ones become available with the level change.
                if (spell.getLevel(plugin) == correctLevel) {
                    player.sendMessage(String.format(plugin.getConfig().getString("messages.learnedSpell"), spell.getDisplayName(plugin)));
                }
            }
        }

        return updated;
    }
    public RpgClass getRpgClass(Player player) {

        if (!main.getPlayerDataConfig().isString("players." + player.getUniqueId() + ".class")){
            setRpgClass(player, RpgClass.NONE);
            return RpgClass.NONE;
        }

        String className = main.getPlayerDataConfig().getString("players." + player.getUniqueId() + ".class");
        for (RpgClass rpgClass : RpgClass.values()) {
            if (rpgClass.toString().equals(className)) return rpgClass;
        }

        return RpgClass.NONE;
    }
    public void setRpgClass(Player player, RpgClass rpgClass) {
        main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".class", rpgClass.toString());
        main.savePlayerDataConfig();
    }
    public boolean isFirstJoin(Player player) {
        if (!plugin.getConfig().isBoolean("players." + player.getUniqueId() + ".firstJoin")) {
            plugin.getLogger().severe("Player " + player.getDisplayName() + " " +
                    "has invalid value for firstJoin in config.yml!");
            return false;
        }

        return main.getPlayerDataConfig().getBoolean("players." + player.getUniqueId() + ".firstJoin");
    }
    public void setFirstJoin(Player player, boolean firstJoin) {
        main.getPlayerDataConfig().set("players." + player.getUniqueId() + ".firstJoin", firstJoin);
        main.savePlayerDataConfig();
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

    // THE FOLLOWING FUNCTIONS WORK CLOSELY WITH THE CLASS UserInterfaceListener TO GIVE THE PLAYER AN INTERFACE TO CHOOSE A CLASS FROM.

    // returns player to 0 sp and classless and calls openClassSelect
    public void resetPlayer(Player player) {
        // initialize player in config
        RpgManager rpgManager = new RpgManager(plugin);
        rpgManager.setSoulPoints(player, 0);
        rpgManager.setRpgClass(player, RpgClass.NONE);
        rpgManager.setFirstJoin(player, true);

        openClassSelect(player);
    }

    // opens the introductory inventory menu to select a class.
    public void openClassSelect(Player player) {
        Inventory selectClass = Bukkit.createInventory(null, 54, plugin.getConfig().getString("lore.intro.inventoryName"));

        // players will hover over this book to read an introduction to LoC.
        ItemStack introBook = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = introBook.getItemMeta();

        itemMeta.setDisplayName(plugin.getConfig().getString("lore.intro.itemName"));
        List<String> lore = plugin.getConfig().getStringList("lore.intro.itemLore");

        itemMeta.setLore(lore);
        introBook.setItemMeta(itemMeta);
        selectClass.setItem(22, introBook);

        // Bukkit crashes if we try to immediately open the inventory after a player joins.
        // adds a delay of 5 ticks.
        class myRunnable implements Runnable {
            private Player player;
            private Inventory inventory;
            private myRunnable(Player player, Inventory inventory) {
                this.player = player;
                this.inventory = inventory;
            }
            public void run() {
                player.openInventory(inventory);
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, new myRunnable(player, selectClass), 5);
    }


    // returns the amount of soul points an entity is worth, based on config file.
    public int getEntitySoulPointValue(Entity entity) {
        // return sp value for mob with custom name if defined in config.yml
        if (entity.getCustomName() != null && plugin.getConfig().isInt("mobsp." + entity.getCustomName())) {
            return plugin.getConfig().getInt("mobsp." + entity.getCustomName());
            // otherwise, return sp value based on what type of mob the entity is
        } else if (plugin.getConfig().isInt("mobsp." + entity.getType().toString())) {
            return plugin.getConfig().getInt("mobsp." + entity.getType().toString());
        }
        // otherwise, return default value
        return plugin.getConfig().getInt("mobsp.default");
    }


}
