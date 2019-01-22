package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.Main;
import ai.chench.legendsofcysendal.util.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

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

        // /party join <partyName>
        if (args[0].equalsIgnoreCase("join")) {
            if (args.length < 2) {
                player.sendMessage("/party join <partyName>");
                return true;
            }
            // attempt to join party
            partyManager.acceptInvite(player, args[1]);
            return true;
        }


        if (args[0].equalsIgnoreCase("invite")) {

            if (args.length < 2) {
                player.sendMessage("/party invite <player>");
                return true;
            }
            Player invited = Bukkit.getPlayer(args[1]);
            if (invited == null) {
                player.sendMessage(main.getConfig().getString("errors.playerNotOnline"));
                return true;
            }
            partyManager.invitePlayer(player, invited);
            return true;
        }


        // /party disband
        if (args[0].equalsIgnoreCase("disband")) {
            partyManager.disbandParty(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            partyManager.removePlayer(player);
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
        player.sendMessage(ChatColor.BLUE + "/party invite <player>:" + ChatColor.WHITE + " Invites a player to the party.");
        player.sendMessage(ChatColor.BLUE + "/party join <name>:" + ChatColor.WHITE + " Join a party if you have been invited to it!.");
        player.sendMessage(ChatColor.BLUE + "/party leave:" + ChatColor.WHITE + " Leave the party you are currently in.");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
    }

    // sends a player their party info, or a note that they are not in a party.
    private void sendPartyInfo(Player player) {
        PartyManager partyManager = new PartyManager(main);

        String partyName = partyManager.getParty(player);

        if (partyName == null) {
            player.sendMessage(main.getConfig().getString("errors.party.notInParty"));
            return;
        }

        List<OfflinePlayer> memberList = partyManager.getMembers(partyName);
        List<OfflinePlayer> inviteList = partyManager.getInviteList(partyName);


        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + partyName);
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");
        player.sendMessage(ChatColor.BLUE + "Leader: " + ChatColor.WHITE + partyManager.getLeader(partyName).getName());
        player.sendMessage(ChatColor.BLUE + "Members: " + ChatColor.GREEN + " (online) " + ChatColor.GRAY + " (offline) ");

        for (OfflinePlayer member : memberList) {
            if (member.isOnline()) {
                player.sendMessage(ChatColor.GREEN + "- " + member.getName());
            } else {
                player.sendMessage(ChatColor.GRAY + "- " + member.getName());
            }

        }

        player.sendMessage(ChatColor.BLUE + "Pending Invitations: ");

        for (OfflinePlayer invited : inviteList) {
            player.sendMessage(ChatColor.GRAY + "- " + invited.getName());
        }

        player.sendMessage(ChatColor.DARK_GRAY + "Type '/party help' for help.");
        player.sendMessage(ChatColor.DARK_GRAY + "=====================");

    }

}
