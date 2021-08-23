package top.seatide.servercore.Utils;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class LogUtil {
    public final static Logger logger = Bukkit.getServer().getLogger();
    // public final static String prefix = "[" + ChatColor.YELLOW + "SEAT" + ChatColor.AQUA + "i" + ChatColor.YELLOW + "DE" + ChatColor.RESET + "]";
    // public final static String ERROR = "[" + ChatColor.RED + "ERROR" + ChatColor.RESET + "] ";
    // public final static String SUCCESS = "[" + ChatColor.GREEN + "SUCCESS" + ChatColor.RESET + "] ";
    // public final static String INFO = "[" + ChatColor.AQUA + "INFO" + ChatColor.RESET + "] ";
    public final static String prefix = "[SEATiDE]";
    public final static String ERROR = "[ERROR] ";
    public final static String SUCCESS = "[SUCCESS] ";
    public final static String INFO = "[INFO] ";

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
