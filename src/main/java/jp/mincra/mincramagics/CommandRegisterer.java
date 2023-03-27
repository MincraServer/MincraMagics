package jp.mincra.mincramagics;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.gui.GUIManager;
import jp.mincra.mincramagics.gui.impl.MaterialEditor;
import jp.mincra.mincramagics.player.MincraPlayer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MagicSkill;
import jp.mincra.mincramagics.skill.MaterialProperty;
import jp.mincra.mincramagics.skill.SkillManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

public class CommandRegisterer {
    public void registerAll() {
        new CommandAPICommand("mincra")
                // /mincra mp <type> <amount>
                .withSubcommand(mpCommand())
                .withSubcommand(skillCommand())
                .withSubcommand(effectCommand())
                .withSubcommand(vfxCommand())
                .withSubcommand(guiCommand())
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
                        case "add" -> mPlayer.getMp().addMp(amount, true);
                        case "sub" -> mPlayer.getMp().subMp(amount);
                        case "set" -> mPlayer.getMp().setMp(amount, true);
                    }
                });
    }

    public CommandAPICommand skillCommand() {
        return new CommandAPICommand("skill")
                .withArguments(new PlayerArgument("caster"))
                .withArguments(new StringArgument("skill_id").replaceSuggestions(ArgumentSuggestions.strings(
                        MincraMagics.getSkillManager().getSkillIds()
                )))
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
                    skill.onTrigger(caster, new MaterialProperty(skillId, skillId, cooldown, consumedMp)
                            .setStrength(strength));
                });
    }

    private CommandAPICommand effectCommand() {
        return new CommandAPICommand("effect")
                .withArguments(new StringArgument("particle"))
                .withArguments(new LocationArgument("pos"))
                .withArguments(new LocationArgument("offset"))
                .withArguments(new FloatArgument("speed"))
                .withArguments(new IntegerArgument("count"))
                .executesPlayer((sender, args) -> {
                    new ParticleBuilder(ParticleEffect.valueOf((String) args[0]), (Location) args[1])
                            .setOffset(((Location) args[2]).toVector())
                            .setSpeed(((Float) args[3]))
                            .setAmount((Integer) args[4])
                            .display();
                });
    }

    private CommandAPICommand vfxCommand() {
        return new CommandAPICommand("vfx")
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(
                        MincraMagics.getVfxManager().getVfxIds()
                )))
                .withArguments(new LocationArgument("loc"))
                .withArguments(new FloatArgument("scale"))
                .withArguments(new LocationArgument("axis"))
                .withArguments(new DoubleArgument("angle"))
                .executes(((sender, args) -> {
                    VfxManager vfxManager = MincraMagics.getVfxManager();
                    vfxManager.getVfx((String) args[0]).playEffect((Location) args[1], (float) args[2],
                            ((Location) args[3]).toVector(), Math.PI * ((double) args[4]) / 180);
                }));
    }

    private CommandAPICommand guiCommand() {
        return new CommandAPICommand("gui")
                .withArguments(new PlayerArgument("target"))
                .withArguments(new StringArgument("id").replaceSuggestions(ArgumentSuggestions.strings(
                        "MaterialEditor"
                )))
                .executes(((sender, args) -> {
                    GUIManager guiManager = MincraMagics.getGuiManager();
                    Player target = (Player) args[0];
                    switch ((String) args[1]) {
                        case "MaterialEditor" -> {
                            guiManager.open(new MaterialEditor(), target);
                            return;
                        }
                    }
                }));
    }

    private void error(Player player, String message) {
        player.sendMessage(Component
                .text(message)
                .color(TextColor.fromHexString("DF847A")));
    }
}
