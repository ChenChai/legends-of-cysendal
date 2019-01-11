package ai.chench.legendsofcysendal.listeners;

import ai.chench.legendsofcysendal.util.RpgClass;
import ai.chench.legendsofcysendal.util.RpgManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

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
            RpgManager rpgManager = new RpgManager(plugin);
            rpgManager.resetPlayer(player);
        }
    }

    // make warn the player if they close the inventory screen before they choose a class.
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        RpgManager rpgManager = new RpgManager(plugin);
        Player player = (Player) event.getPlayer();
        if (isClassSelectScreen(event.getInventory()) && rpgManager.getRpgClass(player) == RpgClass.NONE) {
            player.sendMessage(plugin.getConfig().getString("errors.classNotChosen"));
        }
    }

    // this listener deals with clicks in GUI inventory screens, such as the player selecting a class
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        ItemStack itemStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        // check if the clicked inventory is the same as the intro inventory
        if (isClassSelectScreen(inventory)) {
            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) return;
            String itemName = itemStack.getItemMeta().getDisplayName();

            RpgManager rpgManager = new RpgManager(plugin);

            // check if the intro book was clicked
            if (itemStack.getType() == Material.BOOK && itemStack.getItemMeta().getLore().equals(plugin.getConfig().getStringList("lore.intro.itemLore"))) {
                inventory.clear();
                event.setCancelled(true);
                setupClassSelectInventory(inventory);
                return;
            } else {
                // check if the item name is equal to the item name of a class item in the config file.
                for (RpgClass rpgClass: RpgClass.values())  {
                    String rpgClassItemName = plugin.getConfig().getString("lore.classSelect." + rpgClass.toString() + ".itemName");

                    if (rpgClassItemName != null && itemName != null && rpgClassItemName.equals(itemName)) {
                        rpgManager.setRpgClass(player, rpgClass);
                        player.sendMessage("You are now a " + rpgClass.toString());
                        player.closeInventory();
                        rpgManager.updateLevel(player); // save the level update for until the player has chosen a class, so they see what spells they learn.
                    }
                }
            }

            event.setCancelled(true);
        }
    }

    // updates the inventory with the class selection items
    private void setupClassSelectInventory(Inventory inventory) {
        inventory.setItem(11, makeClassSelectItem(RpgClass.FIGHTER, Material.IRON_AXE));
        inventory.setItem(15, makeClassSelectItem(RpgClass.MAGE, Material.BLAZE_POWDER));
        inventory.setItem(40, makeClassSelectItem(RpgClass.RANGER, Material.BOW));
    }

    // creates an item with lore and display name found from config file.
    private ItemStack makeClassSelectItem(RpgClass rpgClass, Material material) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(plugin.getConfig().getString("lore.classSelect." + rpgClass.toString() + ".itemName"));
        List<String> lore = plugin.getConfig().getStringList("lore.classSelect." + rpgClass.toString() + ".itemLore");
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    // checks if an inventory is the same as the class select inventory, based on inventory name.
    private boolean isClassSelectScreen(Inventory inventory) {
        // remove '§' and the following char from string since these aren't passed in inventoryName for some reason.
        String inventoryName = inventory.getName();
        if (inventoryName!= null)inventoryName = inventoryName.replaceAll("§.", "");

        String compareName = plugin.getConfig().getString("lore.intro.inventoryName").replaceAll("§.", "");

        return inventoryName != null && inventoryName.equals(compareName);

    }
}
