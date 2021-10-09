package top.seatide.servercore.Utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Vault {
    public static Economy eco = null;
    public static Permission perm = null;
    public static Chat chat = null;

    public static boolean setup() {
        var server = Bukkit.getServer();
        if (server.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> ecorsp = server.getServicesManager().getRegistration(Economy.class);
        if (ecorsp == null) {
            return false;
        }
        eco = ecorsp.getProvider();
        // Chat
        RegisteredServiceProvider<Chat> chatrsp = server.getServicesManager().getRegistration(Chat.class);
        if (chatrsp == null) {
            return false;
        }
        chat = chatrsp.getProvider();
        // Permission
        RegisteredServiceProvider<Permission> permrsp = server.getServicesManager().getRegistration(Permission.class);
        if (permrsp == null) {
            return false;
        }
        perm = permrsp.getProvider();
        return true;
    }
}


