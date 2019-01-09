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
        if (rpgManager.getLevel(player) < getSpellLevel(spell)) {
            player.sendMessage(ChatColor.YELLOW + plugin.getConfig().getString("errors.spells.levelTooLow"));
            return false;
        }
        if (!getKnownSpells(player).contains(spell)) {
            player.sendMessage(ChatColor.YELLOW + plugin.getConfig().getString("errors.spells.notKnown"));
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
            player.sendMessage(ChatColor.GOLD + "Consumed " + cost.getAmount() + " " + cost.getType() + " to cast " + getDisplayName(spell));
            return true;
        }
        player.sendMessage(ChatColor.RED + "Insufficient Components! Need " + cost.getAmount() + " " + cost.getType() + " to cast " + getDisplayName(spell));
        return false;
    }

    // returns the display name of a spell from the config
    public String getDisplayName(Spell spell) {
        return plugin.getConfig().getString("spells." + spell.toString() + ".displayName");
    }
    public int getSpellLevel(Spell spell) {
        return plugin.getConfig().getInt("spells." + spell.toString() + ".level");
    }

    public void teachSpell(Player player, Spell spell) {
        List<String> knownSpells = new ArrayList<String>();
        if (plugin.getConfig().isList("players." + player.getUniqueId() + ".spellsKnown")) {
            knownSpells = plugin.getConfig().getStringList("players." + player.getUniqueId() + ".spellsKnown");
        }
        knownSpells.add(spell.toString());
        plugin.getConfig().set("players." + player.getUniqueId() + ".spellsKnown", knownSpells);
        plugin.saveConfig();
    }
    // removes all the spells a player knows
    public void removeAllSpells(Player player) {
        plugin.getConfig().set("players." + player.getUniqueId() + ".spellsKnown", new ArrayList<Spell>());
    }

    // does a player know this spell?
    public boolean isKnownSpell(Player player, Spell spell) {
        for (Spell knownSpell : getKnownSpells(player)) {
            if (spell.equals(knownSpell)) { return true; }
        }
        return false;
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

    // returns a list of the spells a player knows.
    public List<Spell> getKnownSpells(Player player) {
        List<Spell> knownSpells = new ArrayList<Spell>();

        if (plugin.getConfig().isList("players." + player.getUniqueId() + ".spellsKnown")) {

            for (String spellName : plugin.getConfig().getStringList("players." + player.getUniqueId() + ".spellsKnown")) {
                knownSpells.add(Spell.valueOf(spellName));
            }
        }
        return knownSpells;
    }

}
