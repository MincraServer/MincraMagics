package jp.mincra.mincramagics.command;

import com.destroystokyo.paper.ParticleBuilder;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.MincraMagics;
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
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class MincraCommand {
    private enum Permission {
        MINCRA_ADMIN("mincra.admin");

        private final String value;

        Permission(String value) {
            this.value = value;
        }
    }

    public void registerAll() {
        new CommandAPICommand("mincra")
                .withPermission(Permission.MINCRA_ADMIN.value)
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

                    String type = (String) args.get(0);
                    float amount = (float) args.get(1);

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
                    Player caster = (Player) args.get(0);
                    String skillId = (String) args.get(1);

                    if (!skillManager.isRegistered(skillId)) {
                        error(caster, "Skill not found.");
                        return;
                    }

                    float cooldown = (float) args.get(2);
                    int consumedMp = (int) args.get(3);
                    int strength = (int) args.get(4);
                    MagicSkill skill = skillManager.getSkill(skillId);
                    skill.onTrigger(caster, new MaterialProperty(skillId, skillId, cooldown, consumedMp, strength));
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
                    String effectId = (String) args.get(0);
                    Location location = (Location) args.get(1);
                    Location offset = (Location) args.get(2);
                    Float speed = (Float) args.get(3);
                    Integer amount = (Integer) args.get(4);

                    new ParticleBuilder(Particle.valueOf(effectId))
                            .location(location)
                            .offset(offset.x(), offset.y(), offset.z())
                            .extra(speed)
                            .count(amount)
                            .spawn();
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
                    String vfxId = (String) args.get(0);
                    Location location = (Location) args.get(1);
                    float scale = (float) args.get(2);
                    Location axis = (Location) args.get(3);
                    double yaw = (double) args.get(4);

                    VfxManager vfxManager = MincraMagics.getVfxManager();
                    vfxManager.getVfx(vfxId).playEffect(location, scale,
                            axis.toVector(), Math.PI * yaw / 180);
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
                    Player target = (Player) args.get(0);
                    String guiId = (String) args.get(1);

                    switch (guiId) {
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
