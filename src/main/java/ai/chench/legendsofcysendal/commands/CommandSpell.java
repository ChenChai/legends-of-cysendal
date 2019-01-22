package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.Spell;
import ai.chench.legendsofcysendal.util.RpgClass;
import ai.chench.legendsofcysendal.util.RpgManager;
import ai.chench.legendsofcysendal.util.SpellManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CommandSpell implements CommandExecutor {

    Plugin plugin;
    public CommandSpell(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can cast spells. You are not a player.");
            return true;
        }

        Player player = (Player) sender;

        SpellManager spellManager = new SpellManager(player, plugin);
        RpgManager rpgManager = new RpgManager(plugin);

        if (args.length > 0) {
            String spellName = args[0];
            // attempt to cast a spell
            for (Spell spell : Spell.values()) {
                if (spellName.equalsIgnoreCase(spell.toString())) {
                    spellManager.castSpell(spell);
                    return true;
                }
            }
        }


        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "SPELLS");
        player.sendMessage(ChatColor.DARK_GRAY + "Type '/spell' followed by the spell name to cast.");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        if (rpgManager.getRpgClass(player) == RpgClass.NONE) {
            player.sendMessage(ChatColor.BLUE + "You have not yet selected a class! Type '/loc reset' to choose a class.");
        }


        for (Spell spell : spellManager.getClassSpells(rpgManager.getRpgClass(player))){
            if (spellManager.isKnownSpell(player, spell)){
                player.sendMessage(ChatColor.DARK_AQUA + spell.getDisplayName(plugin) + "   [" + spell.toString() + "]");
                ItemStack cost = spellManager.getCost(spell);
                player.sendMessage(ChatColor.BLUE + "   Cost: " + cost.getAmount() + " " + cost.getType());
            } else {
                player.sendMessage(ChatColor.DARK_RED +  spell.getDisplayName(plugin) + " (Unlocked level " + spell.getLevel(plugin) + ")" );
            }
            player.sendMessage(" ");
        }
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        return true;
    }
}
