package jp.mincra.mincramagics.command;


import com.civious.dungeonmmo.events.InventoryItemAction;
import com.civious.dungeonmmo.instances.InstanceStatus;
import com.civious.dungeonmmo.instances.InstancesManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
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
                        sender.sendMessage("§cプレイヤーが見つかりません。");
                        return;
                    }

                    if (dungeonName == null || dungeonName.isEmpty()) {
                        sender.sendMessage("§cダンジョンIDを指定してください。");
                        return;
                    }

                    // dungeonName から始まって かつ player が参加していないインスタンスを探す
                    final var instance = instances.stream()
                            .filter(i -> i.getConfiguration().getInstanceName().startsWith(dungeonName))
                            .filter(i -> i.getStatus() == InstanceStatus.OPEN)
                            .findFirst();

                    if (instance.isEmpty()) {
                        sender.sendMessage("§cダンジョンが満員です");
                        return;
                    }

                    InventoryItemAction.askForDungeonLaunch(player, instance.get().getConfiguration().getInstanceName());
                }));
    }

}
