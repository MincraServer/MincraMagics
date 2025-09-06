package jp.mincra.mincramagics.party;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPostCreateEvent;
import com.alessiodp.parties.api.events.bukkit.party.BukkitPartiesPartyPostDeleteEvent;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostJoinEvent;
import com.alessiodp.parties.api.events.bukkit.player.BukkitPartiesPlayerPostLeaveEvent;
import com.civious.dungeonmmo.dependencies.team.obteam.ValueRegisterUtils;
import com.civious.obteam.mechanics.Team;
import com.civious.obteam.mechanics.TeamManager;
import com.civious.obteam.mechanics.TeamMember;
import com.civious.obteam.mechanics.team_values.TeamValueException;
import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

/**
 * OBTeam を Parties でラップする
 */
public class OBTeamWrapper implements Listener {
    final TeamManager teamManager = TeamManager.getInstance();

    @EventHandler
    private void onPartyCreated(BukkitPartiesPartyPostCreateEvent event) {
        final var creator = event.getCreator();
        if (creator == null) return;

        OBTeamHelper.addTeam(creator.getPlayerUUID());
    }

    @EventHandler
    private void onPartyDeleted(BukkitPartiesPartyPostDeleteEvent event) {
        final var leaderUUID = event.getParty().getLeader();
        if (leaderUUID == null) return;
        final var leader = Bukkit.getPlayer(leaderUUID);

        teamManager.removeTeam(leader);
    }

    @EventHandler
    private void onPlayerJoinToParty(BukkitPartiesPlayerPostJoinEvent event) {
        final var leaderUUID = event.getParty().getLeader();
        if (leaderUUID == null) return;

        OBTeamHelper.addMember(leaderUUID, event.getPartyPlayer().getPlayerUUID());
    }

    @EventHandler
    private void onPlayerLeave(BukkitPartiesPlayerPostLeaveEvent event) {
        final var leaderUUID = event.getParty().getLeader();
        if (leaderUUID == null) return;
        final var leader = Bukkit.getPlayer(leaderUUID);
        final var leaver = Bukkit.getPlayer(event.getPartyPlayer().getPlayerUUID());

        if (leader != null && leader.equals(leaver)) {
            teamManager.removeTeam(leader);
            return;
        }

        teamManager.getTeam(leader).removePlayer(new TeamMember(leaver));
    }

    @EventHandler
    private void onPlayerJoinToServer(PlayerJoinEvent event) {
        final var party = Parties.getApi().getPartyOfPlayer(event.getPlayer().getUniqueId());
        if (party == null) return;

        final var team = teamManager.getTeam(event.getPlayer());
        if (team != null) return; // すでにチームが存在する場合はスキップ

        final var leaderUUID = party.getLeader();
        if (leaderUUID == null) return;

        OBTeamHelper.addTeam(leaderUUID);
        for (var memberUUID : party.getMembers()) {
            if (memberUUID.equals(leaderUUID)) continue;
            OBTeamHelper.addMember(leaderUUID, memberUUID);
        }
    }

    static class OBTeamHelper {
        private static void addTeam(UUID leaderUUID) {
            final var leader = Bukkit.getPlayer(leaderUUID);
            if (leader == null) return;

            final TeamManager teamManager = TeamManager.getInstance();

            if (teamManager.hasTeam(leader)) return;

            try {
                ValueRegisterUtils.registerValue();
            } catch (TeamValueException e) {
                MincraLogger.warn("TeamValue already registered. Skipping...");
            }

            // TeamMember のコンストラクタで TeamMemberValueManager#getValues() が呼ばれるため
            // ValueRegisterUtils#registerValue() を先に呼び出す必要がある
            final var teamMember = new TeamMember(leader);
            teamManager.addTeam(new Team(teamMember));
            teamMember.setValue("player.ready", false);
        }

        private static void removeTeam(UUID leaderUUID) {
            final var leader = Bukkit.getPlayer(leaderUUID);
            if (leader == null) return;

            final TeamManager teamManager = TeamManager.getInstance();

            if (!teamManager.hasTeam(leader)) return;

            teamManager.removeTeam(leader);
        }

        private static void addMember(UUID leaderUUID, UUID memberUUID) {
            final var leader = Bukkit.getPlayer(leaderUUID);
            final var member = Bukkit.getPlayer(memberUUID);

            if (leader == null || member == null) return;

            try {
                ValueRegisterUtils.registerValue();
            } catch (TeamValueException e) {
                MincraLogger.warn("TeamValue already registered. Skipping...");
            }

            // TeamMember のコンストラクタで TeamMemberValueManager#getValues() が呼ばれるため
            // ValueRegisterUtils#registerValue() を先に呼び出す必要がある
            final var teamMember = new TeamMember(member);
            TeamManager.getInstance().getTeam(leader).addMember(teamMember);
            teamMember.setValue("player.ready", false);
        }
    }
}
