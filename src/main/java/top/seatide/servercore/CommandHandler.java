package top.seatide.servercore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import top.seatide.servercore.Tasks.AutoBackup;
import top.seatide.servercore.Tasks.AutoDeletion;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;
import top.seatide.servercore.Utils.Vault;

public class CommandHandler implements TabExecutor {
    public final static String[] ARGS = { "get", "reload", "backup", "rt" };

    public List<String> getResult(String arg, List<String> commands, CommandSender sender) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, commands, result);
        Collections.sort(result);
        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        var result = new ArrayList<String>();
        if (command.getName().equalsIgnoreCase("seatidecore")) {
            if (args.length == 1) {
                var candy = new ArrayList<String>();
                for (var m : ARGS) {
                    if (sender.hasPermission("seatidecore.command." + m)) {
                        candy.add(m);
                    }
                }
                return getResult(args[0], candy, sender);
            }
            if (args.length == 2) {
                if (args[0].equals("get")) {
                    return Arrays.asList("deltime", "backuptime");
                }
            }
        }
        return result;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("seatidecore")) {
            if (args.length == 0) {
                LogUtil.send(sender, "请至少提供一个参数", LogUtil.richERROR);
                return true;
            }
            if (!sender.hasPermission("seatidecore.admin")) {
                if (!sender.hasPermission("seatidecore.command." + args[0])) {
                    LogUtil.send(sender, "你需要 seatidecore." + args[0] + " 权限才能执行此指令", LogUtil.richERROR);
                    return true;
                }
            }
            switch (args[0]) {
                case "get": {
                    if (args.length == 1) {
                        LogUtil.send(sender, "请提供要获取的内容名称", LogUtil.richERROR);
                        return true;
                    }
                    switch (args[1]) {
                        case "deltime": {
                            var delTime = AutoDeletion.maxEmpty - AutoDeletion.currentEmpty;
                            var cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            cal.add(Calendar.SECOND, delTime);
                            var formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                            LogUtil.send(sender, "实例将在 " + delTime + " 秒后（约 &e" + formatted + "&r）释放。");
                            break;
                        }

                        case "backuptime": {
                            var backupTime = AutoBackup.period - AutoBackup.timer;
                            var cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            cal.add(Calendar.SECOND, backupTime);
                            var formatted = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime());
                            LogUtil.send(sender, "下一次备份在 " + backupTime + " 秒后（约 &e" + formatted + "&r）执行。");
                            break;
                        }
                    }
                    break;
                }

                case "backup": {
                    Main.back.doBackup();
                    break;
                }

                case "reload": {
                    Files.reload();
                    Main.del.reload();
                    Main.back.reload();
                    LogUtil.send(sender, "配置文件重载成功", LogUtil.richSUCCESS);
                    break;
                }

                case "rt": {
                    if (!(sender instanceof Player)) {
                        LogUtil.send(sender, "该指令只能由玩家执行。");
                        return true;
                    }
                    var p = (Player) sender;
                    var range = Files.cfg.getInt("random-tp.default");
                    var max = Files.cfg.getInt("random-tp.max");
                    if (args.length >= 2) {
                        try {
                            range = Integer.parseInt(args[1]);
                            if (range > max) {
                                LogUtil.send(p, "&c最大范围不可超过 &e" + max + "&c。");
                                return true;
                            }
                            if (range < 100) {
                                LogUtil.send(p, "&c最小范围不可低于 &e100 &c。");
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            LogUtil.send(p, "&c请输入有效的数字。");
                            return true;
                        }
                    }
                    var rand = getRandomSafeLocation(p, range);
                    if (rand == null) {
                        LogUtil.send(p, "&c暂时无法传送至安全的地点。");
                        return true;
                    }
                    int x = rand[0], y = rand[1], z = rand[2];
                    if (p.teleport(new Location(p.getWorld(), x, y, z))) {
                        LogUtil.send(p, "&a成功随机传送至 &e(" + x + ".0, " + (y + 1) + ".0, " + z + ".0) &a");
                    } else {
                        LogUtil.send(p, "&c传送失败，收取的金额已返还。");
                        Vault.eco.depositPlayer(p, p.getWorld().getName(), Files.cfg.getInt("command-costs.rt"));
                    }
                    break;
                }
            }
        }
        return true;
    }

    private int random(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    private int[] getRandomSafeLocation(Player p, int range) {
        var loc = p.getLocation();
        int x0, z0, x, y, z, tries = 0;
        Block block;
        while (true) {
            if (tries >= 10) {
                return null;
            }
            tries++;
            x0 = (int) loc.getX();
            z0 = (int) loc.getZ();
            x = random(x0 - range, x0 + range);
            z = random(z0 - range, z0 + range);
            block = p.getWorld().getHighestBlockAt((int) x, (int) z);
            if (block.getType() == Material.WATER || block.getType() == Material.LAVA
                    || block.getType() == Material.AIR) {
                continue;
            }
            y = block.getY();
            break;
        }
        int[] result = { x, y + 1, z };
        return result;
    }
}
