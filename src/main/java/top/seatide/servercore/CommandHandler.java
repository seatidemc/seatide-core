package top.seatide.servercore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import top.seatide.servercore.Tasks.AutoBackup;
import top.seatide.servercore.Tasks.AutoDeletion;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;

public class CommandHandler implements TabExecutor {
    public final static String[] ARGS = { "get", "reload", "delete" };

    public List<String> getResult(String arg, List<String> commands) {
        List<String> result = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, commands, result);
        Collections.sort(result);
        return result;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        var result = new ArrayList<String>();
        if (command.getName().equalsIgnoreCase("seatidecore")) {
            if (!sender.hasPermission("seatidecore.admin")) {
                return result;
            }
            if (args.length == 1) {
                return getResult(args[0], Arrays.asList(ARGS));
            } else {
                return result;
            }
        }
        return result;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("seatidecore")) {
            if (!sender.hasPermission("seatidecore.admin")) {
                LogUtil.send(sender, "你需要 seatidecore.admin 权限才能执行此指令", LogUtil.richERROR);
                return true;
            }
            if (args.length == 0) {
                LogUtil.send(sender, "请至少提供一个参数", LogUtil.richERROR);
                return true;
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
            }
        }
        return true;
    }
}
