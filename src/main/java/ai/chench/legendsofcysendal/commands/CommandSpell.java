package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.Spell;
import ai.chench.legendsofcysendal.util.SpellManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandSpell implements CommandExecutor {

    Plugin plugin;
    public CommandSpell(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) { return false; }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can castSpell spells. You are not a player.");
            return true;
        }

        Player player = (Player) sender;
        SpellManager spellManager = new SpellManager(player, plugin);

        String spellName = args[0];

        for (Spell spell : Spell.values()) {
            if (spellName.equalsIgnoreCase(spell.toString())) {
                spellManager.castSpell(spell);
                return true;
            }
        }

        return false;
    }
}
