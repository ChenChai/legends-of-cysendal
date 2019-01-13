package ai.chench.legendsofcysendal.util;

import ai.chench.legendsofcysendal.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyManager {

    Main main;
    public PartyManager(Main main) {
        this.main = main;
    }

    public boolean createParty(Player partyLeader, String partyName) {

        // if the person creating the party is already in a party
        if (main.getPlayerDataConfig().getString("players." + partyLeader.getUniqueId().toString() + ".party", null) != null) {
            partyLeader.sendMessage(ChatColor.YELLOW + main.getConfig().getString("errors.party.alreadyInParty"));
            return false;
        }

        // if a party under the same name is already active
        if (main.getPlayerDataConfig().getBoolean("parties." + partyName + ".active", false)) {
            partyLeader.sendMessage(ChatColor.YELLOW + main.getConfig().getString("errors.party.partyNameTaken"));
            return false;
        }

        // set the party's status to active
        main.getPlayerDataConfig().set("parties." + partyName + ".active", true);

        // add the party creator to the party's member list
        List<String> playerList = new ArrayList<String>();
        playerList.add(partyLeader.getUniqueId().toString());
        main.getPlayerDataConfig().set("parties." + partyName + ".members", playerList);

        // add the party creator as the party leader.
        main.getPlayerDataConfig().set("parties." + partyName + ".leader", partyLeader.getUniqueId().toString());

        // set the party creator's current party to this one.
        main.getPlayerDataConfig().set("players." + partyLeader.getUniqueId().toString() + ".party", partyName);

        main.savePlayerDataConfig();
        return true;
    }


}
