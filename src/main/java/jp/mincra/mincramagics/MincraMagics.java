package jp.mincra.mincramagics;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import jp.mincra.bkvfx.BKVfx;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.command.GuardCommand;
import jp.mincra.mincramagics.command.MincraCommand;
import jp.mincra.mincramagics.gui.GUIManager;
import jp.mincra.mincramagics.hud.HudManager;
import jp.mincra.mincramagics.oraxen.mechanic.gui.GUIMechanicFactory;
import jp.mincra.mincramagics.oraxen.mechanic.artifact.ArtifactMechanicFactory;
import jp.mincra.mincramagics.oraxen.mechanic.material.MaterialMechanicFactory;
import jp.mincra.mincramagics.player.MPRecoverer;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.SkillManager;
import jp.mincra.mincramagics.skill.combat.*;
import jp.mincra.mincramagics.skill.healing.Heal;
import jp.mincra.mincramagics.skill.utility.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class MincraMagics extends JavaPlugin {
    private static MincraMagics INSTANCE;

    private static ProtocolManager protocolManager;
    private static PlayerManager playerManager;
    private static SkillManager skillManager;
    private static MaterialManager materialManager;
    private static HudManager hudManager;
    private static VfxManager vfxManager;
    private static GUIManager guiManager;
    private static Logger logger;

    @Override
    public void onEnable() {
        INSTANCE = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new PlayerManager();
        skillManager = new SkillManager();
        materialManager = new MaterialManager();
        hudManager = new HudManager(playerManager);
        vfxManager = BKVfx.instance().getVfxManager();
        guiManager = new GUIManager(this);
        logger = getLogger();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);
        pluginManager.registerEvents(new MPRecoverer(this, playerManager), this);

        new MincraCommand().registerAll();
        new GuardCommand(getServer()).registerAll();

        // stuff (staff) はタイポしてるけどもう後戻りはできない・・・
        MechanicsManager.registerMechanicFactory("artifact", new ArtifactMechanicFactory("artifact"), true);
        MechanicsManager.registerMechanicFactory("material", new MaterialMechanicFactory("material"), true);
        MechanicsManager.registerMechanicFactory("gui", new GUIMechanicFactory("gui"), true);
        OraxenItems.loadItems();

        // Combat
        skillManager.registerSkill("freeze", new Freeze());
        skillManager.registerSkill("icetree", new IceTree());
        skillManager.registerSkill("inferno", new Inferno());
        skillManager.registerSkill("scorch", new Scorch());
        Snowbomb snowbomb = new Snowbomb();
        skillManager.registerSkill("snowbomb", snowbomb);
        pluginManager.registerEvents(snowbomb, this);

        // Healing
        skillManager.registerSkill("heal", new Heal());

        // Utility
        skillManager.registerSkill("charge", new Charge());
        Jump jumpSkill = new Jump();
        skillManager.registerSkill("jump", jumpSkill);
        pluginManager.registerEvents(jumpSkill, this);
        skillManager.registerSkill("move", new Move());
        Speeden speeden = new Speeden();
        skillManager.registerSkill("speeden", speeden);
        pluginManager.registerEvents(speeden, this);
        skillManager.registerSkill("wraith", new Wraith());
        skillManager.registerSkill("luminous", new Luminous(this));

    }

    @Override
    public void onDisable() {
    }

    public void reload() {
        try {
            // FIXME: onEnable() の中身を切り出して reload() が onEnable() を呼ばないようにする
            onEnable();
            logger.info("MincraMagics reloaded successfully.");
        } catch (Exception e) {
            logger.severe("Failed to reload MincraMagics: " + e.getMessage());
            e.printStackTrace();
        }
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

    public static GUIManager getGuiManager() {
        return guiManager;
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}
