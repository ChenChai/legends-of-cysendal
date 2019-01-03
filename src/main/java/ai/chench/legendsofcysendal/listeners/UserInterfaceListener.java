package ai.chench.legendsofcysendal.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class UserInterfaceListener implements Listener {

    private Plugin plugin;
    public UserInterfaceListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.reloadConfig(); // make sure config up to date
        Player player = event.getPlayer();

        String uniqueId = player.getUniqueId().toString();

        // check if player is registered in config.yml
        // getConfig().isBoolean will return false if the path does not exist because it is the player's first time joining.
        if (!plugin.getConfig().isBoolean("players." + uniqueId + ".firstJoin") || plugin.getConfig().getBoolean("players." + uniqueId + ".firstJoin")) {
            resetPlayer(player);
        }
    }

    // returns player to 0 sp and classless and
    // brings up an inventory interface to choose a new class
    private void resetPlayer(Player player) {
        // initialize player in config
        String uniqueId = player.getUniqueId().toString();
        plugin.getConfig().set("players." + uniqueId + ".sp", 0); // set number of soul points to 0
        plugin.getConfig().set("players." + uniqueId + ".level", 1); // set level to 1
        plugin.getConfig().set("players." + uniqueId + ".class", "none");
        plugin.getConfig().set("players." + uniqueId + ".firstJoin", true); // this is effectively the player's first time joining

        plugin.saveConfig();

        final Inventory selectClass = Bukkit.createInventory(null, 54, plugin.getConfig().getString("lore.intro.inventoryName"));

        // players will hover over this book to read an introduction to LoC.
        ItemStack introBook = new ItemStack(Material.BOOK, 1);
        ItemMeta itemMeta = introBook.getItemMeta();

        itemMeta.setDisplayName(plugin.getConfig().getString("lore.intro.title"));
        List<String> lore = plugin.getConfig().getStringList("lore.intro.text");

        itemMeta.setLore(lore);
        introBook.setItemMeta(itemMeta);
        selectClass.setItem(22, introBook);

        // Bukkit crashes if we try to immediately open the inventory after a player joins.
        // adds a delay of 5 ticks.
        class myRunnable implements Runnable {
            Player player;
            Inventory inventory;
            public myRunnable(Player player, Inventory inventory) {
                this.player = player;
                this.inventory = inventory;
            }
            public void run() {
                player.openInventory(inventory);
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, new myRunnable(player, selectClass), 5);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        ItemStack itemStack = event.getCurrentItem();

        // remove '§' and the following char from string since these aren't passed in inventoryName for some reason.
        String inventoryName = inventory.getName().replaceAll("§.", "");
        String compareName = plugin.getConfig().getString("lore.intro.inventoryName").replaceAll("§.", "");

        Bukkit.broadcastMessage(inventoryName + " was clicked! Comparing to " + compareName);

        // check if the clicked inventory is the same as the intro inventory
        if (inventoryName.equals(compareName)) {
            // check if the intro book was clicked

            Bukkit.broadcastMessage(itemStack.getType() + " was clicked!");
            if (itemStack.getType() == Material.BOOK) {

                inventory.clear();
                event.setCancelled(true);
            }
        }
    }


}
