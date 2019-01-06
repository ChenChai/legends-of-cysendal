package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.util.RpgManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CommandLoc implements CommandExecutor {

    Plugin plugin;

    public CommandLoc(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        for (String arg: args) {
            arg = arg.toLowerCase(); // make arguments lowercase
        }

        RpgManager rpgManager = new RpgManager(plugin);

        // add soul points to a player's total:
        // /addsp <player> <number of points>
        if (label.equals("addsp")) {
            if (args.length < 2) return false; // not enough arguments!
            int points = 0;

            try {
                points = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Number of points must be an integer!");
                return false;
            }

            String playerName = args[0];
            Player player = Bukkit.getPlayer(playerName);
            try {
                player.getUniqueId();
            } catch (NullPointerException e) {
                sender.sendMessage("This player is offline or does not exist!");
                return true;
            }

            rpgManager.addSoulPoints(player, points);
            player.sendMessage(Boolean.toString(rpgManager.updateLevel(player)));

            sender.sendMessage("Added " + points + " soul points to " + playerName + "for a total of " + rpgManager.getSoulPoints(player));
            return true;
        }

        return false;

    }

}
