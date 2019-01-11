package ai.chench.legendsofcysendal.util;

import ai.chench.legendsofcysendal.Spell;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

public class SpellManager {
    Player player;
    Plugin plugin;

    public SpellManager(Player player, Plugin plugin) {
        this.plugin = plugin;
        this.player = player;
    }

    // attempts to cast a spell, trying to consume items and make the spell's effect.
    public boolean castSpell(Spell spell){
        RpgManager rpgManager = new RpgManager(plugin);
        if (!isClassSpell(spell, rpgManager.getRpgClass(player))) {
            player.sendMessage(ChatColor.YELLOW + plugin.getConfig().getString("errors.spells.wrongClass"));
        }
        if (rpgManager.getLevel(player) < spell.getLevel(plugin)) {
            player.sendMessage(ChatColor.YELLOW + plugin.getConfig().getString("errors.spells.levelTooLow"));
            return false;
        }

        if (!consumeCost(player, spell)) {
            return false;
        }

        switch (spell) {
            case LIGHTNING_BOLT: Spell.LIGHTNING_BOLT.makeEffect(player, plugin); break;
            case FLEET_OF_FOOT: Spell.FLEET_OF_FOOT.makeEffect(player, plugin); break;
            default: return false;
        }
        return true;
    }

    // returns an itemstack representing how many resources it takes to castSpell a spell
    public ItemStack getCost(Spell spell) {
        int numItems = plugin.getConfig().getInt("spells." + spell.toString() + ".costItemNum");
        Material itemType = Material.valueOf(plugin.getConfig().getString("spells." + spell.toString() + ".costItemType"));
        return new ItemStack(itemType, numItems);
    }

    // attempts to consume resources needed to castSpell a spell, returns true if enough resources were consumed, false otherwise
    public boolean consumeCost(Player player, Spell spell) {
        ItemStack cost = getCost(spell);
        if (player.getInventory().containsAtLeast(new ItemStack(cost.getType()), cost.getAmount())) {
            int consumed = ItemManager.removeInventoryItemStack(player.getInventory(), cost.getType(), cost.getAmount());
            player.sendMessage(ChatColor.GOLD + "Consumed " + cost.getAmount() + " " + cost.getType() + " to cast " + spell.getDisplayName(plugin));
            return true;
        }
        player.sendMessage(ChatColor.RED + "Insufficient Components! Need " + cost.getAmount() + " " + cost.getType() + " to cast " + spell.getDisplayName(plugin));
        return false;
    }

    // does a player know this spell?
    public boolean isKnownSpell(Player player, Spell spell) {
        RpgManager rpgManager = new RpgManager(plugin);
        if (rpgManager.getLevel(player) < spell.getLevel(plugin)) {
            return false;
        }
        if (!isClassSpell(spell, rpgManager.getRpgClass(player))) {
            return false;
        }
        return true;
    }

    public boolean isClassSpell(Spell spell, RpgClass rpgClass) {
        return plugin.getConfig().getStringList("classes." + rpgClass.toString() + ".spells").contains(spell.toString());
    }

    public List<Spell> getClassSpells(RpgClass rpgClass) {
        List<Spell> classSpells = new ArrayList<Spell>();
        for (String spellName : plugin.getConfig().getStringList("classes." + rpgClass.toString() + ".spells")) {
            classSpells.add(Spell.valueOf(spellName));
        }
        return classSpells;
    }
}
