package top.seatide.servercore.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import top.seatide.servercore.Main;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;
import top.seatide.servercore.Utils.Requests;

public class AutoDeletion {
    public static int maxEmpty = 9999999;
    public static String backupScript = "";
    public static int currentEmpty = 0;
    public BukkitScheduler sche;
    public Runnable task;
    public Requests r;

    public void reload() {
        maxEmpty = Files.cfg.getInt("maxEmptyTime");
        backupScript = Files.cfg.getString("backupScript");
        r = new Requests();
    }

    public AutoDeletion() {
        maxEmpty = Files.cfg.getInt("maxEmptyTime");
        backupScript = Files.cfg.getString("backupScript");
        sche = Bukkit.getServer().getScheduler();
        r = new Requests();
        task = new Runnable(){
            @Override
            public void run() {
                int count = Bukkit.getOnlinePlayers().size();
                if (count == 0) {
                    currentEmpty += 1;
                }
                if (count > 0) {
                    currentEmpty = 0;
                }
                if (maxEmpty - currentEmpty < 60) {
                    LogUtil.info("实例将在 " + (maxEmpty - currentEmpty)
                            + " 秒后释放。");
                }
                if (currentEmpty >= maxEmpty) {
                    if (!backupScript.equals(null)) {
                        LogUtil.info("尝试运行备份脚本...");
                        try {
                            Process p = Runtime.getRuntime().exec(backupScript);
                            int i = p.waitFor();
                            if (i == 0) {
                                LogUtil.success("备份脚本执行成功，返回码 0");
                            } else {
                                LogUtil.error("备份脚本执行失败，返回码 " + i);
                                currentEmpty = 0;
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.error("无法运行备份脚本。");
                            currentEmpty = 0;
                            return;
                        }
                    } else {
                        LogUtil.info("未检测到备份脚本。");
                    }
                    r.deleteInstance();
                    currentEmpty = 0;
                }
            }
        };
    }

    public boolean ready() {
        return maxEmpty > 10 && Requests.site != null;
    }

    public void run(Main plugin) {
        sche.runTaskTimerAsynchronously(plugin, task, 0L, 20L);
    }
}
