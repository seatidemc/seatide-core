package top.seatide.servercore;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;
import top.seatide.servercore.Utils.Vault;

public class Events implements Listener {

    // not very good
    public boolean checkPermission(Player p, String label) {
        return p.hasPermission("essentials.command." + label) || p.hasPermission("seatidecore.command." + label);
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        var p = e.getPlayer();
        String command = e.getMessage(), label = command.split(" ")[0].replaceAll("/", "");
        if (!checkPermission(p, label)) return;
        if (!p.hasPermission("seatidecore.nocommandcost")) {
            var commandSection = Files.cfg.getConfigurationSection("command-costs");
            if (commandSection == null) return;
            int cost;
            for (var c : commandSection.getKeys(false)) {
                if (command.startsWith("/" + c)) {
                    cost = commandSection.getInt(c);
                    if (cost > Vault.eco.getBalance(p)) {
                        LogUtil.send(p, "&c你的余额不足以执行当前指令（单价 &e" + Vault.eco.format(cost) + "&c）");
                        e.setCancelled(true);
                        return;
                    }
                    var r = Vault.eco.withdrawPlayer(p, cost);
                    if (r.type == ResponseType.SUCCESS) {
                        LogUtil.send(p, "&a已向你收取 &e" + Vault.eco.format(r.amount) + "&a, 当前余额 &c"
                                + Vault.eco.format(r.balance) + "&a。");
                    } else if (r.type == ResponseType.FAILURE) {
                        LogUtil.send(p, "&c收取金额时发生错误: " + r.errorMessage);
                    }
                    break;
                }
            }
        }
    }
}
