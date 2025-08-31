package jp.mincra.mincramagics.command;

import dev.jorel.commandapi.CommandAPICommand;
import jp.mincra.mincramagics.gui.screen.JobRewardListMenu;

public class RewardCommand {
    public void registerAll() {
        new CommandAPICommand("reward")
                .executesPlayer((info -> {
                    new JobRewardListMenu().open(info.sender());
                }))
                .register();
    }
}
