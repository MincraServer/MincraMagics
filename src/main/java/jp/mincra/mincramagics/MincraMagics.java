package jp.mincra.mincramagics;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanicFactory;
import jp.mincra.bkvfx.BKVfx;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.command.CommandRegisterer;
import jp.mincra.mincramagics.core.PlayerManager;
import jp.mincra.mincramagics.hud.HudManager;
import jp.mincra.mincramagics.oraxen.mechanic.MagicStuffMechanicFactory;
import jp.mincra.mincramagics.skill.SkillManager;
import jp.mincra.mincramagics.skill.combat.Inferno;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MincraMagics extends JavaPlugin {
    private static MincraMagics INSTANCE;

    private static ProtocolManager protocolManager;
    private static PlayerManager playerManager;
    private static SkillManager skillManager;
    private static HudManager hudManager;
    private static VfxManager vfxManager;

    @Override
    public void onEnable() {
        INSTANCE = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new PlayerManager();
        skillManager = new SkillManager();
        hudManager = new HudManager(playerManager);
        vfxManager = BKVfx.instance().getVfxManager();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);

        skillManager.registerSkill("inferno", new Inferno());

        new CommandRegisterer().registerAll();

        Bukkit.getLogger().info("Is it updated?");
        MechanicsManager.registerMechanicFactory("magicstuff",
                MagicStuffMechanicFactory::new);
        MechanicsManager.registerMechanicFactory("durability2",
                DurabilityMechanicFactory::new);
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

    public static HudManager getHudManager() {
        return hudManager;
    }

    public static VfxManager getVfxManager() {
        return vfxManager;
    }
}
