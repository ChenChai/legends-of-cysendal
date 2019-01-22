package ai.chench.legendsofcysendal.util;

import ai.chench.legendsofcysendal.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private Main main;
    public PartyManager(Main main) {
        this.main = main;
    }

    public boolean createParty(Player partyCreator, String partyName) {

        // if the person creating the party is already in a party
        if (getParty(partyCreator) != null) {
            partyCreator.sendMessage(ChatColor.YELLOW + main.getConfig().getString("errors.party.alreadyInParty"));
            return false;
        }

        // if a party under the same name is already active
        if (isActive(partyName)) {
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

        String partyName = getParty(disbandingPlayer);

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

        // loop through players in the party and set their party to null.
        for (OfflinePlayer member : getMembers(partyName)) {
            main.getPlayerDataConfig().set("players." + member.getUniqueId().toString() + ".party", null);
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
        String partyName =  main.getPlayerDataConfig().getString("players." + player.getUniqueId().toString() + ".party", null);
        if (!isActive(partyName)) {
            return null;
        }
        return partyName;
    }

    public OfflinePlayer getLeader(String partyName) {
        return Bukkit.getOfflinePlayer(UUID.fromString(main.getPlayerDataConfig().getString("parties." + partyName + ".leader", null)));
    }

    // returns a list of the players in a party
    public List<OfflinePlayer> getMembers(String partyName) {
        if (!isActive(partyName)) { return null; }

        // get list of players' UUIDs
        List<String> playerUidList = main.getPlayerDataConfig().getStringList("parties." + partyName + ".members");

        List<OfflinePlayer> playerList = new ArrayList<OfflinePlayer>();

        // convert UUIDs to players
        for (String uid : playerUidList) {
            playerList.add(Bukkit.getOfflinePlayer(UUID.fromString(uid)));
        }

        return playerList;
    }

    // returns a list of the players who have been invited to a party
    public List<OfflinePlayer> getInviteList(String partyName) {
        if (!isActive(partyName)) { return null; }

        // get list of players' UUIDs
        List<String> playerUidList = main.getPlayerDataConfig().getStringList("parties." + partyName + ".invites");

        List<OfflinePlayer> playerList = new ArrayList<OfflinePlayer>();

        // convert UUIDs to players
        for (String uid : playerUidList) {
            playerList.add(Bukkit.getOfflinePlayer(UUID.fromString(uid)));
        }

        return playerList;
    }


    private boolean isActive(String partyName) {
        return main.getPlayerDataConfig().getBoolean("parties." + partyName + ".active", false);
    }

    // attempts to let player accept an invitation to a party. Returns true if player joined successfully.
    public boolean acceptInvite(Player invited, String partyName) {
        if (getParty(invited) != null) {
            invited.sendMessage(main.getConfig().getString("errors.party.alreadyInParty"));
            return false;
        }

        // check if party player is trying to join exists
        if (!isActive(partyName)) {
            invited.sendMessage(main.getConfig().getString("errors.party.partyDoesNotExist"));
            return false;
        }

        // check if player is on the invitation list
        List<OfflinePlayer> inviteList = getInviteList(partyName);

        // if player is not on the invitation list
        if (!inviteList.contains((OfflinePlayer) invited)) {
            invited.sendMessage(main.getConfig().getString("errors.party.notInvited"));
            return false;
        }

        // take player off the invited list
        inviteList.remove(invited);
        setInviteList(partyName, inviteList);

        // add player to the member list
        List<OfflinePlayer> memberList = getMembers(partyName);
        memberList.add((OfflinePlayer) invited);
        setMembers(partyName, memberList);

        // change player's party to this one.
        main.getPlayerDataConfig().set("players." + invited.getUniqueId().toString() + ".party", partyName);

        // tell all members someone new joined
        for (OfflinePlayer member : memberList) {

            if (member.isOnline()) {
                ((Player) member).sendMessage(String.format(main.getConfig().getString("messages.party.joinedParty"), invited.getName(),partyName));
            }
        }
        main.savePlayerDataConfig();

        return true;
    }

    private void setInviteList(String partyName, List<OfflinePlayer> inviteList) {
        List<String> uidList = new ArrayList<String>();

        for (OfflinePlayer player : inviteList) {
            uidList.add(player.getUniqueId().toString());
        }
        main.getPlayerDataConfig().set("parties." + partyName + ".invites", uidList);
        main.savePlayerDataConfig();
    }

    private void setMembers(String partyName, List<OfflinePlayer> memberList) {
        List<String> uidList = new ArrayList<String>();

        for (OfflinePlayer player : memberList) {
            uidList.add(player.getUniqueId().toString());
        }
        main.getPlayerDataConfig().set("parties." + partyName + ".members", uidList);
        main.savePlayerDataConfig();
    }

    // attempts to have a player invite another player into the party.
    public boolean invitePlayer(Player inviter, Player invited) {
        String partyName = getParty(inviter);

        if (partyName == null) {
            inviter.sendMessage(main.getConfig().getString("errors.party.notInParty"));
            return false;
        }

        // if inviter is not the leader.
        if (!getLeader(partyName).equals(inviter)) {
            inviter.sendMessage(main.getConfig().getString("errors.party.notPartyLeader"));
            return false;
        }

        // get the current invitation list
        List<OfflinePlayer> inviteList = getInviteList(partyName);
        inviteList.add(invited); // add the player to the invitation list.

        List<String> uidList = new ArrayList<String>();

        for (OfflinePlayer p : inviteList) {
            uidList.add(p.getUniqueId().toString());
        }

        main.getPlayerDataConfig().set("parties." + partyName + ".invites", uidList);

        main.savePlayerDataConfig();

        inviter.sendMessage(String.format(main.getConfig().getString("messages.party.inviteSent"), invited.getDisplayName()));
        invited.sendMessage(String.format(main.getConfig().getString("messages.party.inviteReceived"), partyName, inviter.getDisplayName(), partyName));

        return true;
    }

    // immediately removes an online player from the party if they are not the leader
    public boolean removePlayer(Player player) {
        String partyName = getParty(player);

        // check if player is in party
        if (!isActive(partyName)) {
            player.sendMessage(main.getConfig().getString("errors.party.notInParty"));
            return false;
        }

        // check if player is the leader of the party
        // the leader of the party cannot leave—only disband the party.
        if (player.equals(getLeader(partyName))) {
            player.sendMessage(main.getConfig().getString("errors.party.leaderCannotLeave"));
            return false;
        }

        // update the list of members
        List<OfflinePlayer> memberList = getMembers(partyName);
        memberList.remove(player);
        setMembers(partyName, memberList);

        // set the player's party to nothing
        main.getPlayerDataConfig().set("players." + player.getUniqueId().toString() + ".party", null);

        // tell the party that a player left.
        for (OfflinePlayer member : getMembers(partyName)) {
            if (member.isOnline()) {
                ((Player) member).sendMessage(String.format(main.getConfig().getString("messages.party.leftParty"), player.getName(), partyName));
            }
        }

        // need to tell the player separately as they are no longer on the party list, so will not receive the previous message.
        player.sendMessage(String.format(main.getConfig().getString("messages.party.leftParty"), player.getName(), partyName));
        main.savePlayerDataConfig();
        return true;
    }

    // attempts to remove a player from the party if the kicker is the party leader.
    // will work on offline players, as it will check a string against the party name
    public boolean kickOfflinePlayer(Player kicker, String kicked) {
        String partyName = getParty(kicker);
        if (!isActive(partyName)) {
            kicker.sendMessage(main.getConfig().getString("errors.party.notInParty"));
            return false;
        }

        if (!kicker.equals(getLeader(partyName))) {
            kicker.sendMessage(main.getConfig().getString("errors.party.notPartyLeader"));
            return false;
        }

        List<OfflinePlayer> memberList = getMembers(partyName);

        // check the member list for someone to kick
        for (OfflinePlayer member : memberList) {
            if (member.getName().equalsIgnoreCase(kicked)) {
                // leader cannot kick themselves
                if (kicker.equals(member)) {
                    kicker.sendMessage(main.getConfig().getString("errors.party.leaderCannotLeave"));
                    return false;
                }

                memberList.remove(member);
                main.getPlayerDataConfig().set("players." + member.getUniqueId() + ".party", null);
                main.savePlayerDataConfig();

                kicker.sendMessage(String.format(main.getConfig().getString("messages.party.kickSuccess"), member.getName()));
                if (member.isOnline()) {
                    ((Player) member).sendMessage(String.format(main.getConfig().getString("messages.party.youWereKicked"), member.getName()));
                }

                setMembers(partyName, memberList);
                return true;
            }
        }

        List<OfflinePlayer> inviteList = getInviteList(partyName);
        // check the member list for someone to kick
        for (OfflinePlayer invited : inviteList) {
            if (invited.getName().equalsIgnoreCase(kicked)) {
                inviteList.remove(invited); // remove player from invite list
                kicker.sendMessage(String.format(main.getConfig().getString("messages.party.inviteWithdrawSuccess"), invited.getName()));
                if (invited.isOnline()) {
                    ((Player) invited).sendMessage(String.format(main.getConfig().getString("messages.party.yourInviteWasWithdrawn"), partyName));
                }

                setInviteList(partyName, inviteList);
                return true;
            }
        }

        kicker.sendMessage(main.getConfig().getString("errors.party.memberNotFound"));
        return false;
    }
}
