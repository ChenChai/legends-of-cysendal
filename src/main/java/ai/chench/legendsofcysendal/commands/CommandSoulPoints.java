package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.util.RpgManager;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

// this class handles commands that deal with interacting with the number of soul points players have
// /getsp, /addsp
public class CommandSoulPoints implements CommandExecutor {

    Plugin plugin;

    public CommandSoulPoints(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // the first argument of this set of commands will always be <player>
        // validate the first argument as an online player
        if (args.length < 1) return false; // no first argument
        String playerName = args[0];
        Player player = Bukkit.getPlayer(playerName);
        try {
            player.getUniqueId();
        } catch (NullPointerException e) {
            sender.sendMessage("This player is offline or does not exist!");
            return true;
        }

        // make command lowercase
        label = label.toLowerCase();
        for (String arg: args) {
            arg = arg.toLowerCase();
        }

        RpgManager rpgManager = new RpgManager(plugin);

        // get the number of soul points a player has
        // /getsp <player>
        if (label.equals("getsp")) {

            sender.sendMessage(player.getDisplayName() + " has " + rpgManager.getSoulPoints(player)  + " Soul Points, and is level "
            + rpgManager.getLevel(player));
            return true;
        }

        // add soul points to a player's total:
        // /addsp <player> <number of points>
        if (label.equals("addsp")) {
            if (args.length < 2) return false; // not enough arguments!
            int points = 0;

            try { // attempt to convert second argument into an integer
                points = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Number of points must be an integer!");
                return false;
            }

            rpgManager.addSoulPoints(player, points);

            sender.sendMessage("Added " + points + " soul points to " + playerName + " for a total of " + rpgManager.getSoulPoints(player));
            if (rpgManager.updateLevel(player)) { // check if the player has levelled up due to the added points.
                sender.sendMessage(player.getDisplayName() + " has levelled up to level " + rpgManager.getLevel(player));
            }
            return true;
        }





        return false;

    }

}
