package jp.mincra.mincramagics;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.bukkit.Location;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class CommandManager {
    public static void register() {
        new CommandAPICommand("mincra")
                .withArguments(new StringArgument("particle"))
                .withArguments(new LocationArgument("pos"))
                .withArguments(new LocationArgument("offset"))
                .withArguments(new FloatArgument("speed"))
                .withArguments(new IntegerArgument("count"))
                .executesPlayer((PlayerCommandExecutor) (sender, args) -> {
                    new ParticleBuilder(ParticleEffect.valueOf((String) args[0]), (Location) args[1])
                            .setOffset(((Location) args[2]).toVector())
                            .setSpeed(((Float) args[3]))
                            .setAmount((Integer) args[4])
                            .display();
                })
                .register();
    }

}
