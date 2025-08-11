package jp.mincra.mincramagics.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class RegionId {
    private final String player;
    private final String id;

    public RegionId(Player player, String id) {
        this.player = player.getName();
        this.id = id;
    }

    public static String withoutPlayerId(Player player, String regionId) {
        return regionId.substring(player.getName().length() + 1);
    }

    public String getPlayer() {
        return player;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return player + "-" + id;
    }
}

public class GuardCommand {
    private enum Permission {
        GUARD_GENERAL("guard.general");

        private final String value;

        Permission(String value) {
            this.value = value;
        }
    }

    private final Server server;
    private final WorldGuardPlugin worldGuardPlugin;
    private final RegionContainer regionContainer;

    public GuardCommand(Server server) {
        this.server = server;
        this.worldGuardPlugin = WorldGuardPlugin.inst();
        WorldGuard worldGuard = WorldGuard.getInstance();
        this.regionContainer = worldGuard.getPlatform().getRegionContainer();
    }

    public void registerAll() {
        new CommandAPICommand("guard")
                .withPermission(Permission.GUARD_GENERAL.value)
                .withSubcommand(addCommand())
                .withSubcommand(removeCommand())
                .withSubcommand(listCommand())
                .register();
    }

    private CommandAPICommand addCommand() {
        return new CommandAPICommand("add")
                .withArguments(new StringArgument("id"))
                .withOptionalArguments(new StringArgument("height_setting").replaceSuggestions(ArgumentSuggestions.strings(
                        "default", "expand_y"
                )))
                .executesPlayer(((sender, args) -> {
                    String id = (String) args.get(0);
                    String heightSetting = (String) args.getOrDefault(1, "default");

                    if (heightSetting.equals("expand_y")) {
                        server.dispatchCommand(sender, "/expand vert");
                    }

                    CommandSender consoleSender = server.getConsoleSender();
                    
                    // 一時的に権限を付与してからコマンドを実行
                    server.dispatchCommand(consoleSender, "lp user " + sender.getName() + " permission set worldguard.region.claim true");
                    server.dispatchCommand(sender, "region claim " + new RegionId(sender, id));
                    server.dispatchCommand(consoleSender, "lp user " + sender.getName() + " permission unset worldguard.region.claim true");
                }));
    }

    private CommandAPICommand removeCommand() {
        return new CommandAPICommand("remove")
                .withArguments(new StringArgument("id").replaceSuggestions(
                        ArgumentSuggestions.strings((info) -> {
                            CommandSender sender = info.sender();

                            if (sender instanceof Player player) {
                                return getRegionsOf(player).stream()
                                        .map(region -> RegionId.withoutPlayerId(player, region.getId()))
                                        .toArray(String[]::new);
                            }
                            return new String[0];
                        })))
                .executesPlayer(((sender, args) -> {
                    String id = (String) args.get(0);

                    server.dispatchCommand(sender, "region remove " + new RegionId(sender, id));
                }));
    }

    private CommandAPICommand listCommand() {
        return new CommandAPICommand("list")
                .executesPlayer(((sender, args) -> {
                    var gray = TextColor.fromHexString("#AAAAAA");
                    var message = Component.text("-------").color(gray)
                            .append(Component.text("土地保護一覧").color(gray))
                            .append(Component.text("-------").color(gray))
                            .appendNewline();
                    var regions = getRegionsOf(sender);

                    for (var region : regions) {
                        var white = TextColor.fromHexString("#FFFFFF");
                        message = message.append(Component.text(RegionId.withoutPlayerId(sender, region.getId())).color(white).appendNewline());
                    }
                    message = message.append(Component.text("-----------------------").color(gray));

                    sender.sendMessage(message);
                }));
    }

    private List<ProtectedRegion> getRegionsOf(Player owner) {
        List<ProtectedRegion> results = new ArrayList<>();
        RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(owner.getWorld()));

        if (regionManager == null) return results;

        Map<String, ProtectedRegion> regions = regionManager.getRegions();
        for (ProtectedRegion region : regions.values()) {
            if (region.isOwner(worldGuardPlugin.wrapPlayer(owner))) {
                results.add(region);
            }
        }

        return results;
    }
}
