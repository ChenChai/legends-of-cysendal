package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.util.RpgManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
            // TODO: Reset player
        }

        return true;
    }

    // displays a player's progression information to them in chat.
    private void displayLocInfo(Player player) {
        RpgManager rpgManager = new RpgManager(plugin);
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "LEGENDS OF CYSENDAL");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage("Class: " + rpgManager.getClass(player).toString());
        // sends additional portion of message if player is max level.
        player.sendMessage("Level: " + rpgManager.getLevel(player) + (rpgManager.getLevel(player) >= rpgManager.getMaxLevel() ? " (MAX LEVEL)" : ""));
        player.sendMessage("Soul Points: " + rpgManager.getSoulPoints(player) +
                (rpgManager.getLevel(player) >= rpgManager.getMaxLevel() ? "" : "/" + rpgManager.getSoulPointsForLevel(rpgManager.getLevel(player))));
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
    }

    public CommandLoc(Plugin plugin) {
        this.plugin = plugin;
    }

}
