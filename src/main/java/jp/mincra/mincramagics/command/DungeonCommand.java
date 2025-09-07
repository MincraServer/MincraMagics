package jp.mincra.mincramagics.command;


import com.alessiodp.parties.api.Parties;
import com.civious.dungeonmmo.events.InventoryItemAction;
import com.civious.dungeonmmo.instances.InstanceStatus;
import com.civious.dungeonmmo.instances.InstancesManager;
import com.civious.dungeonmmo.utils.PlayerStatusUtils;
import com.civious.obteam.mechanics.TeamManager;
import com.civious.obteam.mechanics.TeamMember;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DungeonCommand {
    private enum Permission {
        DUNGEON_START("dungeon.start");

        private final String value;

        Permission(String value) {
            this.value = value;
        }
    }

    private final InstancesManager manager;

    public DungeonCommand() {
        manager = InstancesManager.getInstance();
    }

    public void registerAll() {
        new CommandAPICommand("dungeon")
                .withSubcommand(startCommand())
                .withSubcommand(readyCommand())
                .register();
    }

    private CommandAPICommand startCommand() {
        final var instances = manager.getDungeonInstances();
        return new CommandAPICommand("start")
                .withPermission(Permission.DUNGEON_START.value)
                .withArguments(new PlayerArgument("player"))
                .withArguments(new StringArgument("dungeon_id").replaceSuggestions(ArgumentSuggestions.strings(
                        instances.stream().map(i -> i.getConfiguration().getInstanceName().replaceAll(
                                // "_[0-9]+ を削除
                                "_[0-9]+$", ""
                        )).toList()
                )))
                .executes(((sender, args) -> {
                    final var player = (Player) args.get(0);
                    final var dungeonName = (String) args.get(1);

                    if (player == null) {
                        sender.sendMessage("§c◆ プレイヤーが見つかりません。");
                        return;
                    }

                    if (dungeonName == null || dungeonName.isEmpty()) {
                        sender.sendMessage("§c◆ ダンジョンIDを指定してください。");
                        return;
                    }

                    // dungeonName から始まって かつ player が参加していないインスタンスを探す
                    final var instance = instances.stream()
                            .filter(i -> i.getConfiguration().getInstanceName().startsWith(dungeonName))
                            .filter(i -> i.getStatus() == InstanceStatus.OPEN)
                            .findFirst();

                    if (instance.isEmpty()) {
                        sender.sendMessage("§c◆ ダンジョンが満員です");
                        return;
                    }

//                    final var teamManager = TeamManager.getInstance();
//                    final var team = teamManager.getTeam(player);
                    final var party = Parties.getApi().getPartyOfPlayer(player.getUniqueId());
                    if (party == null) {
                        sender.sendMessage("§c◆ /party create 名前 でパーティを作成してからダンジョンに参加してください。");
                        return;
                    }

                    // Add all party members to the team
//                    for (var partyMemberUUID : party.getMembers()) {
//                        final var partyMember = Bukkit.getPlayer(partyMemberUUID);
//                        var teamMember = team.getMember(partyMember);
//                        if (teamMember == null) {
//                            teamMember = new TeamMember(partyMember);
//                            team.addMember(teamMember);
//                        }
//                        teamMember.setValue("player.ready", true);
//                    }

                    InventoryItemAction.askForDungeonLaunch(player, instance.get().getConfiguration().getInstanceName());
                }));
    }

    private CommandAPICommand readyCommand() {
        return new CommandAPICommand("ready")
                .executesPlayer(((player, args) -> {
                    final var teamManager = TeamManager.getInstance();
                    final var team = teamManager.getTeam(player);
                    if (team == null) {
                        player.sendMessage("§c◆ パーティを組んでから実行してください。");
                        return;
                    }

                    if (PlayerStatusUtils.isReady(player)) {
                        player.sendMessage("§a◆ 既に準備完了になっています。");
                        return;
                    }
                    PlayerStatusUtils.setReady(player, true);
                    player.sendMessage("§a◆ 準備を完了しました。");
                }));
    }

}
