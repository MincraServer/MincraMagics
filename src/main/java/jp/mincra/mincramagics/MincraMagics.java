package jp.mincra.mincramagics;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import jp.mincra.bkvfx.BKVfx;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.hud.HudManager;
import jp.mincra.mincramagics.oraxen.mechanic.magicstuff.MagicStuffMechanicFactory;
import jp.mincra.mincramagics.oraxen.mechanic.material.MaterialMechanicFactory;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.SkillManager;
import jp.mincra.mincramagics.skill.combat.Inferno;
import jp.mincra.mincramagics.skill.utility.Charging;
import jp.mincra.mincramagics.skill.utility.Jump;
import jp.mincra.mincramagics.skill.utility.Move;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MincraMagics extends JavaPlugin {
    private static MincraMagics INSTANCE;

    private static ProtocolManager protocolManager;
    private static PlayerManager playerManager;
    private static SkillManager skillManager;
    private static MaterialManager materialManager;
    private static HudManager hudManager;
    private static VfxManager vfxManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new PlayerManager();
        skillManager = new SkillManager();
        materialManager = new MaterialManager();
        hudManager = new HudManager(playerManager);
        vfxManager = BKVfx.instance().getVfxManager();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);

        new CommandRegisterer().registerAll();

        MechanicsManager.registerMechanicFactory("magicstuff", MagicStuffMechanicFactory::new);
        MechanicsManager.registerMechanicFactory("material", MaterialMechanicFactory::new);

        skillManager.registerSkill("inferno", new Inferno());
        skillManager.registerSkill("charging", new Charging());
        skillManager.registerSkill("move", new Move());
        Jump jumpSkill = new Jump();
        skillManager.registerSkill("jump", jumpSkill);
        pluginManager.registerEvents(jumpSkill, this);
    }

    @Override
    public void onDisable() {
    }

    public static MincraMagics getInstance() {
        return INSTANCE;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    public static SkillManager getSkillManager() {
        return skillManager;
    }

    public static MaterialManager getMaterialManager() {
        return materialManager;
    }

    public static HudManager getHudManager() {
        return hudManager;
    }

    public static VfxManager getVfxManager() {
        return vfxManager;
    }
}
