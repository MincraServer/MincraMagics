package jp.mincra.mincramagics.db;

import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.db.model.JobReward;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.io.File;
import java.util.Properties;

/**
 * Singleton utility class to manage the Hibernate SessionFactory.
 * Reads database configuration from a provided YamlConfiguration.
 */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {}

    /**
     * Initializes the SessionFactory using the plugin's configuration.
     * This method should be called once in the onEnable() method of the plugin.
     *
     * @param plugin The JavaPlugin instance.
     * @param config The YamlConfiguration containing database settings.
     */
    public static void initialize(JavaPlugin plugin, YamlConfiguration config) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            MincraLogger.warn("Hibernate SessionFactory is already initialized.");
            return;
        }

        try {
            if (!plugin.getDataFolder().exists()) {
                boolean succeeded = plugin.getDataFolder().mkdirs();
                if (!succeeded) {
                    MincraLogger.error("Failed to create data folder for the plugin.");
                    return;
                }
            }

            // Get database filename from the provided config object
            String dbFileName = config.getString("database.filename", "rewards.db");
            File dbFile = new File(plugin.getDataFolder(), dbFileName);
            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

            Configuration configuration = new Configuration();

            Properties settings = new Properties();
            settings.put(Environment.DRIVER, "org.sqlite.JDBC");
            settings.put(Environment.URL, jdbcUrl);
            settings.put(Environment.DIALECT, "org.hibernate.community.dialect.SQLiteDialect");

            // Get debug setting from config
            boolean showSql = config.getBoolean("debug", false);
            settings.put(Environment.SHOW_SQL, String.valueOf(showSql));

            settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
            settings.put(Environment.HBM2DDL_AUTO, "update");

            configuration.setProperties(settings);
            configuration.addAnnotatedClass(JobReward.class);

            sessionFactory = configuration.buildSessionFactory();
            MincraLogger.info("Hibernate SessionFactory initialized successfully for " + jdbcUrl);

        } catch (Exception e) {
            MincraLogger.error("Failed to initialize Hibernate SessionFactory!", e);
        }
    }

    // ... (getSessionFactory and shutdown methods remain the same)
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new IllegalStateException("SessionFactory has not been initialized or has been closed. Call initialize() first.");
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}