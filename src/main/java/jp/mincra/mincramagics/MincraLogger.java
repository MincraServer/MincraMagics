package jp.mincra.mincramagics;

import io.lumine.mythic.bukkit.utils.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

public class MincraLogger {
    private enum LogLevel {
        DEBUG(TextColor.fromHexString("#8a8a8a")),
        INFO(TextColor.fromHexString("#03a5fc")),
        WARN(TextColor.fromHexString("#ffe657")),
        ERROR(TextColor.fromHexString("#ff4d4d")),
        FATAL(TextColor.fromHexString("#ff0000"));

        private final TextColor color;

        LogLevel(TextColor color) {
            this.color = color;
        }

        public TextColor getColor() {
            return color;
        }
    }

    private static final Component MINCRA_COMPONENT = MiniMessage.miniMessage().deserialize("<gradient:#C7FFFF:#1CE4EC>MincraMagics</gradient>");

    private static Component prefix(LogLevel level) {
        return Component.text("[").color(TextColor.fromHexString("#FFFFFF"))
                .append(MINCRA_COMPONENT)
                .append(Component.text(":").color(TextColor.fromHexString("#FFFFFF")))
                .append(Component.text(level.name()).color(level.getColor()))
                .append(Component.text("] ").color(TextColor.fromHexString("#FFFFFF")));
    }

    private static void send(LogLevel level, String message) {
        MincraMagics.getAudiences().sender(Bukkit.getConsoleSender()).sendMessage((ComponentLike)
                prefix(level).append(Component.text(message, level.getColor()))
        );
    }

    public static void debug(String message) {
        if (MincraMagics.isDebug()) {
            send(LogLevel.DEBUG, message);
        }
    }

    public static void info(String message) {
        send(LogLevel.INFO, message);
    }

    public static void warn(String message) {
        send(LogLevel.WARN, message);
    }

    public static void error(String message) {
        send(LogLevel.ERROR, message);
    }

    public static void error(String message, Exception e) {
        send(LogLevel.ERROR, message + " - Exception: " + e.getMessage());
        e.printStackTrace();
    }

    public static void fatal(String message) {
        send(LogLevel.FATAL, message);
    }
}
