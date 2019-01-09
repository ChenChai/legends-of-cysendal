package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.util.Party;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class CommandParty implements CommandExecutor {

    Plugin plugin;
    public CommandParty(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can make parties. You are now a player.");
            return true;
        }
        Player player = (Player) sender;

        Party party = new Party(player, plugin);

        if (0 == args.length || args[0].equalsIgnoreCase("info")) {
            if (party.getPartyLeader() == null) {
                player.sendMessage("You are not currently in a party.");
                return true;
            }
            player.sendMessage(ChatColor.DARK_GRAY + "=====================");
            player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "PARTY");
            player.sendMessage(ChatColor.DARK_GRAY + "=====================");
            player.sendMessage("Party Leader: " + party.getPartyLeader().getDisplayName());
            player.sendMessage("Party Members: ");
            for (Player member : party.getPartyPlayers()) {
                player.sendMessage(ChatColor.DARK_GRAY + "   " + member.getDisplayName());
            }
            player.sendMessage(ChatColor.DARK_GRAY + "=====================");
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (party.getPartyLeader() != null) {
                player.sendMessage("You are already in a party. Use '/party leave' or '/party disband' first!");
            } else {
                Party newParty = new Party(player, plugin); // create a new party with the player as its leader
                newParty.setPartyLeader(player);
                player.sendMessage("Party created!");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disband")) {

            if (party.getPartyLeader().getUniqueId().equals(player.getUniqueId())) {
                party.disband();
                player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Party Disbanded!");
            } else {
                player.sendMessage(ChatColor.RED + "Only the party leader can disband parties. Use '/party leave' to leave the party.");
            }
            return true;
        }
        return false;
    }
}
