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
import jp.mincra.mincramagics.oraxen.mechanic.magicstuff.MagicStuffMechanicFactory;
import jp.mincra.mincramagics.oraxen.mechanic.material.MaterialMechanicFactory;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.SkillManager;
import jp.mincra.mincramagics.skill.combat.Freeze;
import jp.mincra.mincramagics.skill.combat.Inferno;
import jp.mincra.mincramagics.skill.combat.Scorch;
import jp.mincra.mincramagics.skill.combat.Snowbomb;
import jp.mincra.mincramagics.skill.healing.Heal;
import jp.mincra.mincramagics.skill.utility.Charge;
import jp.mincra.mincramagics.skill.utility.Jump;
import jp.mincra.mincramagics.skill.utility.Move;
import jp.mincra.mincramagics.skill.utility.Wraith;
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
    private static GUIManager guiManager;

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

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);

        new MincraCommand().registerAll();
        new GuardCommand(getServer()).registerAll();

        MechanicsManager.registerMechanicFactory("magicstuff", new MagicStuffMechanicFactory("magicstuff"), true);
        MechanicsManager.registerMechanicFactory("material", new MaterialMechanicFactory("material"), true);
        MechanicsManager.registerMechanicFactory("gui", new GUIMechanicFactory("gui"), true);
        OraxenItems.loadItems();

        // Combat
        skillManager.registerSkill("freeze", new Freeze());
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
        skillManager.registerSkill("wraith", new Wraith());

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

    public static GUIManager getGuiManager() {
        return guiManager;
    }
}
