package jp.mincra.mincramagics.command;


import com.alessiodp.parties.api.Parties;
import com.civious.dungeonmmo.api.events.DungeonPlayerLeaveEvent;
import com.civious.dungeonmmo.events.InventoryItemAction;
import com.civious.dungeonmmo.instances.InstanceStatus;
import com.civious.dungeonmmo.instances.InstancesManager;
import com.civious.dungeonmmo.utils.PlayerStatusUtils;
import com.civious.obteam.mechanics.TeamManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import jp.mincra.mincramagics.MincraLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DungeonCommand implements Listener {
    private enum Permission {
        DUNGEON_START("dungeon.start"),
        DUNGEON_BACK("dungeon.back");

        private final String value;

        Permission(String value) {
            this.value = value;
        }
    }

    private final InstancesManager manager;
    private final Map<UUID, Location> previousLocations = new HashMap<>();

    public DungeonCommand(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        manager = InstancesManager.getInstance();
    }

    public void registerAll() {
        new CommandAPICommand("dungeon")
                .withSubcommand(startCommand())
                .withSubcommand(readyCommand())
                .withSubcommand(backCommand())
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

                    // パーティーメンバー = チームメンバーの状態にする
                    // パーティーに入っているのにチームに入っていない or パーティーメンバー以外がチームに入っている場合がある

//                    // 1. パーティーに入っているのにチームが存在しない場合は新規作成
//                    final var teamManager = TeamManager.getInstance();
//                    var team = teamManager.getTeam(player);
//                    if (team == null) {
//                        team = new Team(new TeamMember(player));
//                        teamManager.addTeam(team);
//                    }
//
//                    // 2. パーティーメンバー以外がチームに入っている場合は削除
//                    final var teamMembers = team.getMembers();
//                    final var membersToRemove = teamMembers.stream()
//                            .filter(tm -> !party.getMembers().contains(tm.getOfflinePlayer().getUniqueId()))
//                            .toList();
//                    for (var memberToRemove : membersToRemove) {
//                        team.removePlayer(memberToRemove);
//                    }
//
//                    // 3. パーティーメンバーがチームに入っていない場合は追加
//                    final var membersToAdd = party.getMembers().stream()
//                            .filter(uuid -> teamMembers.stream().noneMatch(tm -> tm.getOfflinePlayer().getUniqueId().equals(uuid)))
//                            .toList();
//                    for (var uuid : membersToAdd) {
//                        final var partyMember = Bukkit.getPlayer(uuid);
//                        if (partyMember != null) {
//                            team.addMember(new TeamMember(partyMember));
//                        }
//                    }
//
//                    // 4. setValue("player.ready", true) を全員に付与
//                    try {
//                        ValueRegisterUtils.registerValue();
//                    } catch (TeamValueException e) {
//                        MincraLogger.warn("TeamValue already registered. Skipping...");
//                    }
//
//                    for (var  teamMember : team.getMembers()) {
//                        teamMember.setValue("player.ready", true);
//                    }

                    final var conf = instance.get().getConfiguration();
                    previousLocations.put(party.getId(), player.getLocation());
                    InventoryItemAction.askForDungeonLaunch(player, conf.getInstanceName());
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

    /**
     * 奈落落下時にプレイヤーをダンジョンの入り口へ戻すコマンド
     * @return CommandAPICommand
     */
    private CommandAPICommand backCommand() {
        return new CommandAPICommand("back")
                .withPermission(Permission.DUNGEON_BACK.value)
                .withArguments(new PlayerArgument("player"))
                .withHelp("Teleport the player back to their previous location before entering the dungeon.", "/dungeon back <player>")
                .executes((caster, args) -> {
                    // プレイヤーを元いた場所に返す

                    // プレイヤーが所属しているパーティーを取得
                    final var player = (Player) args.get(0);
                    if (player == null) {
                        caster.sendMessage("§c◆ プレイヤーが見つかりません。");
                        return;
                    }

                    final var party = Parties.getApi().getPartyOfPlayer(player.getUniqueId());
                    if (party == null) {
                        caster.sendMessage("§c◆ プレイヤーがパーティーに所属していません。");
                        return;
                    }

                    final var previousLocation = previousLocations.get(party.getId());
                    if (previousLocation == null) {
                        caster.sendMessage("§c◆ 元の場所が記録されていません。");
                        return;
                    }

                    player.teleport(previousLocation);
                });
    }

    @EventHandler
    private void onDungeonLeave(DungeonPlayerLeaveEvent event) {
        final var player = event.getPlayer();
        MincraLogger.info("Player " + player.getName() + " is leaving the dungeon.");

        final var previousLocation = previousLocations.remove(player.getUniqueId());
        if (previousLocation != null) {
            player.teleport(previousLocation);
            previousLocations.remove(player.getUniqueId());
            MincraLogger.info("Teleported " + player.getName() + " back to their previous location.");
        }
    }
}
