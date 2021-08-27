package top.seatide.servercore.Utils;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class LogUtil {
    public final static Logger logger = Bukkit.getServer().getLogger();
    public final static String richPrefix = "[" + ChatColor.YELLOW + "SEAT" + ChatColor.AQUA + "i" + ChatColor.YELLOW + "DE" + ChatColor.RESET + "]";
    public final static String richERROR = "[" + ChatColor.RED + "ERROR" + ChatColor.RESET + "] ";
    public final static String richSUCCESS = "[" + ChatColor.GREEN + "SUCCESS" + ChatColor.RESET + "] ";
    public final static String richINFO = "[" + ChatColor.AQUA + "INFO" + ChatColor.RESET + "] ";
    public final static String prefix = "[SEATiDE]";
    public final static String ERROR = "[ERROR] ";
    public final static String SUCCESS = "[SUCCESS] ";
    public final static String INFO = "[INFO] ";

    public static String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void send(CommandSender p, String msg) {
        var a = richPrefix;
        if (!(p instanceof Player)) {
            a = prefix;
            msg = msg.replaceAll("&(\\d|k|m|n|o|l|a|b|c|d|e|f)", "");
        }
        p.sendMessage(translate(a + " " + msg));
    }

    public static void send(CommandSender p, String msg, String pref) {
        var a = richPrefix;
        var b = pref;
        if (!(p instanceof Player)) {
            a = prefix;
            b = b.replace("&", "");
            msg = msg.replace("&", "");
        }
        p.sendMessage(translate(a + b + msg));
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
