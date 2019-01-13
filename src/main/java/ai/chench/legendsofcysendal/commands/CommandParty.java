package ai.chench.legendsofcysendal.commands;

import ai.chench.legendsofcysendal.Main;
import ai.chench.legendsofcysendal.util.PartyManager;
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
            return false;
        }

        Player player = (Player) sender;

        // /party create <partyName>
        if (args.length >= 2 && args[0].equalsIgnoreCase("create")) {
            PartyManager partyManager = new PartyManager(main);
            partyManager.createParty(player, args[1]);

            player.sendMessage(main.getConfig().getString("messages.party.partyCreated"));
            return true;
        }


        if (args.length >= 1 && args[0].equalsIgnoreCase("disband")) {

        }



        return false;
    }



}
