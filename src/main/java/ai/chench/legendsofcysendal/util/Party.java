package ai.chench.legendsofcysendal.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// this class handles party configuration. A party is a group of players.
// Parties are not objects; the party a player is in is simply the leader's UID, stored in the config file.

// a party leader will have a partyList entry in YAML, containing a list of UIDs of players in the party they lead.
// a player in a party will have a partyLeader entry, containing the UID of the player leading the party, or null if they are not in a party
public class Party {
    private Player partyLeader;
    private Plugin plugin;

    // constructs an object representing the party the player is in.
    public Party(Player player, Plugin plugin) {
        this.plugin = plugin;

        if (player != null && plugin.getConfig().isString("players." + player.getUniqueId().toString() + ".partyLeader")) {
            this.partyLeader = (Player) Bukkit.getOfflinePlayer(UUID.fromString(plugin.getConfig().getString("players." + player.getUniqueId().toString() + ".partyLeader")));
        } else {
            this.partyLeader = null;
        }
    }

    public Player getPartyLeader() {
        return partyLeader;
    }

    // updates the leader of the party, changing each player's partyLeader config entry as well.
    // also
    public void setPartyLeader(Player leader) {
        List<Player> playerList = getPartyPlayers();

        partyLeader = leader;
        changeToThisParty(leader);
        for (Player follower : playerList) {
            changeToThisParty(follower);
        }

    }

    // changes a player's party to the current one.
    public void changeToThisParty(Player player) {
        // remove player from the old party they are in if it exists
        Party oldParty = new Party(player, plugin);
        if (oldParty.partyLeader != null) {
            List<Player> oldPartyList = oldParty.getPartyPlayers();
            oldPartyList.remove(player);
            oldParty.setPartyPlayers(oldPartyList);
        }

        if (player != null) {
            // change player's partyLeader config to say they are in the new party
            plugin.getConfig().set("players." + player.getUniqueId().toString() + ".partyLeader", partyLeader == null ? null : partyLeader.getUniqueId().toString());
            plugin.saveConfig();

        }
        // update the new party's list to
        List<Player> playerList = getPartyPlayers();
        if (!playerList.contains(player)) { playerList.add(player); }
        setPartyPlayers(playerList);
    }

    // gets a list of the players in a party. May be an empty list if player not in a party.
    public List<Player> getPartyPlayers() {

        List<Player> playerList = new ArrayList<Player>();

        List<String> partyUids = new ArrayList<String>();
        if (partyLeader != null && plugin.getConfig().isList("players." + partyLeader.getUniqueId() + ".partyList")) {
            partyUids = plugin.getConfig().getStringList("players." + partyLeader.getUniqueId() + ".partyList");
        }

        for (String uidString : partyUids) {
            playerList.add((Player) Bukkit.getOfflinePlayer(UUID.fromString(uidString)));
        }

        return playerList;
    }

    public void setPartyPlayers(List<Player> playerList) {
        List<String> partyUids = new ArrayList<String>();
        for (Player p : playerList) {
            if (p != null && p.getUniqueId() != null) {
                partyUids.add(p.getUniqueId().toString());
                plugin.getConfig().set("players." + p.getUniqueId().toString() + ".partyLeader", (partyLeader == null ? null : partyLeader.getUniqueId().toString()));
            }


        }

        if (partyLeader != null && partyLeader.getUniqueId() != null) {
            plugin.getConfig().set("players." + partyLeader.getUniqueId().toString() + ".partyList", partyUids);
        }
        plugin.saveConfig();
    }

    public void disband() {
        setPartyLeader(null); //TODO: fix this
        setPartyPlayers(new ArrayList<Player>());
    }
}
