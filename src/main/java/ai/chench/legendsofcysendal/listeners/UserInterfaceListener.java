package ai.chench.legendsofcysendal.listeners;

import ai.chench.legendsofcysendal.util.RpgManager;
import org.apache.commons.lang.ObjectUtils;
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
        RpgManager rpgManager = new RpgManager(plugin);
        rpgManager.setSoulPoints(player, 0);
        rpgManager.updateLevel(player);
        rpgManager.setClass(player, "none");
        rpgManager.setFirstJoin(player, true);

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

    // this listener deals with clicks in GUI inventory screens, such as the player selecting a class
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        ItemStack itemStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        // remove '§' and the following char from string since these aren't passed in inventoryName for some reason.
        String inventoryName = inventory.getName().replaceAll("§.", "");

        String compareName = plugin.getConfig().getString("lore.intro.inventoryName").replaceAll("§.", "");

        // check if the clicked inventory is the same as the intro inventory
        if (inventoryName.equals(compareName)) {
            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) return;
            String itemName = itemStack.getItemMeta().getDisplayName();

            RpgManager rpgManager = new RpgManager(plugin);

            // check if the intro book was clicked
            if (itemStack.getType() == Material.BOOK && itemStack.getItemMeta().getLore().equals(plugin.getConfig().getStringList("lore.intro.itemLore"))) {
                inventory.clear();
                event.setCancelled(true);
                setupClassSelectInventory(inventory);
                return;
            } else if (itemName.equals(plugin.getConfig().getString("lore.classSelect.fighter.itemName"))){
                Bukkit.broadcastMessage("Fighter!");
                rpgManager.setClass(player, "fighter");

            } else if (itemName.equals(plugin.getConfig().getString("lore.classSelect.mage.itemName"))){
                Bukkit.broadcastMessage("Mage!");
                rpgManager.setClass(player, "mage");
            } else if (itemName.equals(plugin.getConfig().getString("lore.classSelect.ranger.itemName"))){
                Bukkit.broadcastMessage("Ranger!");
                rpgManager.setClass(player,"ranger");
            }

            event.setCancelled(true);
        }
    }

    // updates the inventory with the class selection items
    private void setupClassSelectInventory(Inventory inventory) {
        inventory.setItem(1, makeClassSelectItem("fighter", Material.IRON_AXE));
        inventory.setItem(2, makeClassSelectItem("mage", Material.BLAZE_POWDER));
        inventory.setItem(3, makeClassSelectItem("ranger", Material.BOW));
    }

    // creates an item with lore and display name found from config file.
    private ItemStack makeClassSelectItem(String className, Material material) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(plugin.getConfig().getString("lore.classSelect." + className + ".itemName"));
        List<String> lore = plugin.getConfig().getStringList("lore.classSelect." + className + ".itemLore");
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
