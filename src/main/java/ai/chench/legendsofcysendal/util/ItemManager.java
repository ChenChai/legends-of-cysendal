package ai.chench.legendsofcysendal.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemManager {

    public static int removeInventoryItemStack(Inventory inventory, Material material, int amount) {
        if (amount <= 0) return 0;

        int numRemoved = 0;
        for (ItemStack itemStack : inventory) {
            if (itemStack != null && itemStack.getType().equals(material)) {
                int newAmount = itemStack.getAmount() - amount;
                if (newAmount <= 0) {
                    numRemoved += itemStack.getAmount();
                    amount -= itemStack.getAmount();
                    itemStack.setAmount(0);
                } else {
                    itemStack.setAmount(newAmount);
                    numRemoved += amount;
                    amount = 0;
                    return numRemoved;
                }
            }
        }
        return numRemoved;
    }
}
