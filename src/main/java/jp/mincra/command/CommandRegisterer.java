package jp.mincra.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import jp.mincra.MincraMagics;
import jp.mincra.core.MincraPlayer;
import jp.mincra.core.PlayerManager;

public class CommandRegisterer {
    public void registerAll() {
        new CommandAPICommand("mincra")
                // /mincra mp <type> <amount>
                .withSubcommand(new CommandAPICommand("mp")
                        .withArguments(new StringArgument("type")
                                .replaceSuggestions(ArgumentSuggestions.strings("add", "sub", "set")),
                                new FloatArgument("amount"))
                        .executesPlayer((player, args) -> {
                            PlayerManager playerManager = MincraMagics.getPlayerManager();
                            MincraPlayer mPlayer = playerManager.getPlayer(player.getUniqueId());

                            String type = (String) args[0];
                            float amount = (float) args[1];

                            switch (type) {
                                case "add":
                                    mPlayer.addMp(amount, true);
                                    break;
                                case "sub":
                                    mPlayer.subMp(amount);
                                    break;
                                case "set":
                                    mPlayer.setMp(amount, true);
                                    break;
                            }
                        }))
                .register();
    }
}
