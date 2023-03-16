package jp.mincra.mincramagics.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.core.MincraPlayer;
import jp.mincra.mincramagics.core.PlayerManager;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.SkillManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

public class CommandRegisterer {
    public void registerAll() {
        new CommandAPICommand("mincra")
                // /mincra mp <type> <amount>
                .withSubcommand(mpCommand())
                .withSubcommand(skillCommand())
                .register();
    }

    public CommandAPICommand mpCommand() {
        return new CommandAPICommand("mp")
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
                            mPlayer.getMp().addMp(amount, true);
                            break;
                        case "sub":
                            mPlayer.getMp().subMp(amount);
                            break;
                        case "set":
                            mPlayer.getMp().setMp(amount, true);
                            break;
                    }
                });
    }

    public CommandAPICommand skillCommand() {
        return new CommandAPICommand("skill")
                .withArguments(new PlayerArgument("caster"))
                .withArguments(new StringArgument("skill_id"))
                .withArguments(new FloatArgument("cooldown"))
                .withArguments(new IntegerArgument("consumed_mp"))
                .withArguments(new IntegerArgument("strength"))
                .executesPlayer((sender, args) -> {
                    SkillManager skillManager = MincraMagics.getSkillManager();
                    Player caster = (Player) args[0];
                    String skillId = (String) args[1];

                    if (!skillManager.isRegistered(skillId)) {
                        error(caster, "Skill not found.");
                        return;
                    }

                    float cooldown = (float) args[2];
                    int consumedMp = (int) args[3];
                    int strength = (int) args[4];
                    MagicSkill skill = skillManager.getSkill(skillId);
                    skill.onTrigger(caster, new MaterialProperty(
                            cooldown, consumedMp, strength, skillId
                    ));
                });
    }

    private void error(Player player, String message) {
        player.sendMessage(Component
                .text(message)
                .color(TextColor.fromHexString("DF847A")));
    }
}
