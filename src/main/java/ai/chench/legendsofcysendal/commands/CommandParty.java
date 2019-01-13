package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.Main;
import ai.chench.legendsofcysendal.util.PartyManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandParty implements CommandExecutor {

    private Main main;
    public CommandParty(Main main) {
        this.main = main;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a player. Only players can manage parties.");
        }

        Player player = (Player) sender;
        PartyManager partyManager = new PartyManager(main);

        if (args.length == 0) {
            sendPartyInfo(player);
            return true;
        }
        // /party info
        if (args[0].equalsIgnoreCase("info")) {
            sendPartyInfo(player);
            return true;
        }

        // /party create <partyName>
        if (args[0].equalsIgnoreCase("create")) {

            // if sender did not specify a party name
            if (args.length < 2) {
                player.sendMessage("/party create <partyName>");
                return true;
            }

            partyManager.createParty(player, args[1]); // attempt to create a party.
            return true;
        }


        // /party disband
        if (args[0].equalsIgnoreCase("disband")) {
            partyManager.disbandParty(player);
            return true;
        }

        sendPartyInstructions(player);
        return true;
    }

    // gives the player a party help menu.
    private void sendPartyInstructions(Player player) {
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.BOLD + "PARTY HELP");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.BLUE + "/party help:" + ChatColor.WHITE + " Brings up help menu.");
        player.sendMessage(ChatColor.BLUE + "/party create <name>:" + ChatColor.WHITE + " Creates a party with the specified name. Only one party can have a specific name at a time.");
        player.sendMessage(ChatColor.BLUE + "/party disband:" + ChatColor.WHITE + " Disbands a party. Only party leaders can use this command.");
        player.sendMessage(ChatColor.BLUE + "/party info:" + ChatColor.WHITE + " Displays information about your party.");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
    }

    // sends a player their party info, or a note that they are not in a party.
    private void sendPartyInfo(Player player) {
        PartyManager partyManager = new PartyManager(main);

        String partyName = partyManager.getParty(player);

        if (partyName == null) {
            player.sendMessage("errors.party.notInParty");
            return;
        }


        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + partyName);
        player.sendMessage(ChatColor.DARK_GRAY + "Type '/party help' for help.");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.DARK_GREEN + "Leader: " + partyManager.getPartyLeader(partyName).getDisplayName());

        player.sendMessage(ChatColor.DARK_GRAY + "=====================");

    }

}
