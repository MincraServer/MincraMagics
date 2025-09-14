package jp.mincra.mincramagics;

import com.civious.dungeonmmo.api.DungeonMMOAPI;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import jp.mincra.bkvfx.BKVfx;
import jp.mincra.bkvfx.VfxManager;
import jp.mincra.mincramagics.command.DungeonCommand;
import jp.mincra.mincramagics.command.GuardCommand;
import jp.mincra.mincramagics.command.MincraCommand;
import jp.mincra.mincramagics.command.RewardCommand;
import jp.mincra.mincramagics.config.ConfigManager;
import jp.mincra.mincramagics.db.HibernateUtil;
import jp.mincra.mincramagics.db.dao.JobRewardDao;
import jp.mincra.mincramagics.gui.GUIManager;
import jp.mincra.mincramagics.hud.DamageIndicator;
import jp.mincra.mincramagics.hud.HudManager;
import jp.mincra.mincramagics.oraxen.mechanic.artifact.ArtifactMechanicFactory;
import jp.mincra.mincramagics.oraxen.mechanic.gui.GUIMechanicFactory;
import jp.mincra.mincramagics.oraxen.mechanic.material.MaterialMechanicFactory;
import jp.mincra.mincramagics.party.OBTeamWrapper;
import jp.mincra.mincramagics.player.MPRegenerate;
import jp.mincra.mincramagics.player.MPRepository;
import jp.mincra.mincramagics.player.PlayerManager;
import jp.mincra.mincramagics.skill.MaterialManager;
import jp.mincra.mincramagics.skill.SkillManager;
import jp.mincra.mincramagics.skill.combat.*;
import jp.mincra.mincramagics.skill.healing.Heal;
import jp.mincra.mincramagics.skill.passive.*;
import jp.mincra.mincramagics.skill.utility.*;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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
    private static ConfigManager configManager;
    private static BukkitAudiences audiences;
    private static DamageIndicator damageIndicator;

    private static JobRewardDao jobRewardDao;

    @Override
    public void onEnable() {
        INSTANCE = this;

        // Managers
        audiences = BukkitAudiences.create(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        playerManager = new PlayerManager();
        skillManager = new SkillManager();
        materialManager = new MaterialManager();
        configManager = new ConfigManager(this);
        hudManager = new HudManager(playerManager, configManager);
        vfxManager = BKVfx.instance().getVfxManager();
        damageIndicator = new DamageIndicator(this);

        // Initialize database
        HibernateUtil.initialize(this, configManager.getConfig("config.yml"));
        jobRewardDao = new JobRewardDao(HibernateUtil.getSessionFactory());

        // DB に依存する Manager はここで初期化
        guiManager = new GUIManager();

        // Event Listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(playerManager, this);
        pluginManager.registerEvents(hudManager, this);
        pluginManager.registerEvents(new MPRegenerate(this, playerManager), this);
        pluginManager.registerEvents(new MPRepository(playerManager), this);
        pluginManager.registerEvents(damageIndicator, this);
        pluginManager.registerEvents(new OBTeamWrapper(), this);

        // Command API
        // CommandAPI.onLoad(new CommandAPIConfig().initializeNBTAPI());
        new MincraCommand().registerAll();
        new GuardCommand(getServer()).registerAll();
        new RewardCommand().registerAll();
        new DungeonCommand().registerAll();

        // Mechanics
        MechanicsManager.registerMechanicFactory("artifact", new ArtifactMechanicFactory("artifact"), true);
        MechanicsManager.registerMechanicFactory("material", new MaterialMechanicFactory("material"), true);
        MechanicsManager.registerMechanicFactory("gui", new GUIMechanicFactory("gui"), true);
        OraxenItems.loadItems();

        // Skills
        // combat
        skillManager.registerSkill("freeze", new Freeze());
        skillManager.registerSkill("icetree", new IceTree());
        skillManager.registerSkill("inferno", new Inferno());
        skillManager.registerSkill("scorch", new Scorch());
        skillManager.registerSkill("snowbomb", new Snowbomb());
        skillManager.registerSkill("resistance", new Resistance());
        skillManager.registerSkill("icetree_snowball", new IceTreeSnowball());
        skillManager.registerSkill("lightning", new Lightning());
        skillManager.registerSkill("beast_spawn", new BeastSpawn());
        skillManager.registerSkill("mechanics", new Mechanics());
        // Healing
        skillManager.registerSkill("heal", new Heal());
        // Passive
        skillManager.registerSkill("hp_boost", new HpBoost());
        skillManager.registerSkill("mp_boost", new MpBoost());
        skillManager.registerSkill("hp_recovery", new HpRecovery());
        skillManager.registerSkill("mp_recovery", new MpRecovery());
        skillManager.registerSkill("bleeding", new Bleeding());

        // Utility
        skillManager.registerSkill("charge", new Charge());
        skillManager.registerSkill("charging", new Charging());
        skillManager.registerSkill("jump", new Jump());
        skillManager.registerSkill("move", new Move());
        skillManager.registerSkill("speeden", new Speeden());
        skillManager.registerSkill("wraith", new Wraith());
        skillManager.registerSkill("luminous", new Luminous(this));
        skillManager.registerSkill("mine", new Mine());
        skillManager.registerSkill("water_move", new WaterMove());
        // Job
        // - Hunter
        skillManager.registerSkill("burst", new Burst());
        skillManager.registerSkill("protect", new Protect());
        skillManager.registerSkill("provoke", new Provoke());
        // - Miner
        skillManager.registerSkill("mineral_detection", new MineralDetection());
        skillManager.registerSkill("mine_all", new MineAll());
        skillManager.registerSkill("hammer", new Hammer());

        // check if DungeonMMO is present
        if (getServer().getPluginManager().getPlugin("DungeonMMO") != null) {
            DungeonMMOAPI.getInstance().registerItemProvider("Oraxen", s -> {
                final var builder = OraxenItems.getItemById(s);
                if (builder == null) {
                    MincraLogger.warn("DungeonMMO: Oraxen item not found: " + s);
                    return null;
                }
                return builder.build();
            });
        }
    }

    @Override
    public void onDisable() {
        damageIndicator.removeAll();
    }

    public void reload() {
        configManager.reloadConfigs();
        MechanicsManager.registerMechanicFactory("artifact", new ArtifactMechanicFactory("artifact"), true);
        MechanicsManager.registerMechanicFactory("material", new MaterialMechanicFactory("material"), true);
        MechanicsManager.registerMechanicFactory("gui", new GUIMechanicFactory("gui"), true);
        OraxenItems.loadItems();
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

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static BukkitAudiences getAudiences() {
        return audiences;
    }

    public static JobRewardDao getJobRewardDao() {
        return jobRewardDao;
    }

    public static boolean isDebug() {
        return getInstance().getConfig().getBoolean("debug", false);
    }
}
