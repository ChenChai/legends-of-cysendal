package ai.chench.legendsofcysendal.util;

import ai.chench.legendsofcysendal.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// this class handles the creation and management of parties, which are lists of players with a leader, stored in configuration.
// a player's party is stored as a party name in their config. Party names are unique, and a party is identified by its party name.
// party data is stored in the playerData.yml as well, under the heading 'parties.<partyName>' each party entry has a list of players,
// the party's leader, and a party's pending invites.

// How the party looks in YAML
// parties:
//   party_name_here:
//     active: true
//     leader: uuid_string_1
//     members:
//     - uuid_string_1
//     - uuid_string_2
//     invites:
//     - uuid_string_3


public class PartyManager {

    Main main;
    public PartyManager(Main main) {
        this.main = main;
    }

    public boolean createParty(Player partyCreator, String partyName) {

        // if the person creating the party is already in a party
        if (main.getPlayerDataConfig().getString("players." + partyCreator.getUniqueId().toString() + ".party", null) != null) {
            partyCreator.sendMessage(ChatColor.YELLOW + main.getConfig().getString("errors.party.alreadyInParty"));
            return false;
        }

        // if a party under the same name is already active
        if (main.getPlayerDataConfig().getBoolean("parties." + partyName + ".active", false)) {
            partyCreator.sendMessage(ChatColor.YELLOW + main.getConfig().getString("errors.party.partyNameTaken"));
            return false;
        }

        // set the party's status to active
        main.getPlayerDataConfig().set("parties." + partyName + ".active", true);

        // add the party creator to the party's member list
        List<String> playerList = new ArrayList<String>();
        playerList.add(partyCreator.getUniqueId().toString());
        main.getPlayerDataConfig().set("parties." + partyName + ".members", playerList);

        // add the party creator as the party leader.
        main.getPlayerDataConfig().set("parties." + partyName + ".leader", partyCreator.getUniqueId().toString());

        // set the party creator's current party to this one.
        main.getPlayerDataConfig().set("players." + partyCreator.getUniqueId().toString() + ".party", partyName);

        main.savePlayerDataConfig();

        partyCreator.sendMessage(main.getConfig().getString("messages.party.partyCreated"));
        return true;
    }

    // attempts to disband the player's party. Only works if they are the party leader.
    public boolean disbandParty(Player disbandingPlayer) {
        String playerUid = disbandingPlayer.getUniqueId().toString();

        String partyName = main.getPlayerDataConfig().getString("players." + playerUid + ".party", null);

        // check if the player is actually in a party.
        if (partyName == null) {
            disbandingPlayer.sendMessage(main.getConfig().getString("errors.party.notInParty"));
            return false;
        }

        // check if the player is the party leader.
        String leaderUid = main.getPlayerDataConfig().getString("parties." + partyName + ".leader");
        if (!playerUid.equalsIgnoreCase(leaderUid)) {
            disbandingPlayer.sendMessage(main.getConfig().getString("errors.party.notPartyLeader"));
            return false;
        }

        main.getPlayerDataConfig().set("parties." + partyName + ".leader", null);
        main.getPlayerDataConfig().set("parties." + partyName + ".invites", null);
        main.getPlayerDataConfig().set("parties." + partyName + ".members", null);
        main.getPlayerDataConfig().set("parties." + partyName + ".active", false);

        main.getPlayerDataConfig().set("players." + playerUid + ".party", null);

        main.savePlayerDataConfig();

        disbandingPlayer.sendMessage(main.getConfig().getString("messages.party.partyDisbanded"));
        return true;
    }

    // returns the name of the party the player is in, or null if the player is not in a party.
    public String getParty(Player player) {
        return main.getPlayerDataConfig().getString("players." + player.getUniqueId().toString() + ".party", null);
    }
}
