package top.seatide.servercore.Utils;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class LogUtil {
    public final static Logger logger = Bukkit.getServer().getLogger();
    public final static String prefix = "[&eSEAT&bi&eDE&r]";
    public final static String ERROR = "[&cERROR&r] ";
    public final static String SUCCESS = "[&aSUCCESS&r] ";
    public final static String INFO = "[&bINFO&r] ";

    public static String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void send(Player p, String msg) {
        p.sendMessage(translate(prefix + msg));
    }

    public static void log(String msg) {
        logger.info(translate(msg));
    }

    public static void info(String msg) {
        log(prefix + INFO + msg);
    }

    public static void error(String msg) {
        log(prefix + ERROR + msg);
    }

    public static void success(String msg) {
        log(prefix + SUCCESS + msg);
    }
}
