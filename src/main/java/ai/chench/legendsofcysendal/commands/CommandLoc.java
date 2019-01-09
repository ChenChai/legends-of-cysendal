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
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class CommandLoc implements CommandExecutor {
    private Plugin plugin;
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = null;

        if (sender instanceof Player) {

            player = (Player) sender;
        } else {
            sender.sendMessage("Only players have info associated with Legends of Cysendal. You are not a player.");
            return true;
        }

        if (args.length == 0) {
            displayLocInfo(player);
        } else if (args[0].equalsIgnoreCase("info")) {
            displayLocInfo(player);
        } else if (args[0].equalsIgnoreCase("reset")) {
            RpgManager rpgManager = new RpgManager(plugin);

            // Double check that the player wants to reset by typing confirm
            if (rpgManager.getRpgClass(player) == RpgClass.NONE || args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
                rpgManager.resetPlayer(player);
            } else {
                player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ARE YOU SURE YOU WANT TO RESET YOUR CLASS, LOSING ALL SOUL POINTS AND LEVELS?");
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "TYPE '/loc reset confirm' TO RESET.");
            }
        }


        return true;
    }

    // displays a player's progression information to them in chat.
    private void displayLocInfo(Player player) {
        RpgManager rpgManager = new RpgManager(plugin);
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "LEGENDS OF CYSENDAL");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.DARK_GREEN + "Class: " + ChatColor.WHITE + "" + ChatColor.BOLD + "" + rpgManager.getRpgClass(player).getDisplayName(plugin));
        // sends additional portion of message if player is max level.
        player.sendMessage(ChatColor.AQUA + "Level: " + ChatColor.WHITE + "" + ChatColor.BOLD + "" + rpgManager.getLevel(player) + (rpgManager.getLevel(player) >= rpgManager.getMaxLevel() ? " (MAX LEVEL)" : ""));
        player.sendMessage(ChatColor.DARK_AQUA + "Soul Points: " + ChatColor.WHITE + "" + rpgManager.getSoulPoints(player) +
                (rpgManager.getLevel(player) >= rpgManager.getMaxLevel() ? "" : "/" + rpgManager.getSoulPointsForLevel(rpgManager.getLevel(player))));
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
    }

    public CommandLoc(Plugin plugin) {
        this.plugin = plugin;
    }

}
