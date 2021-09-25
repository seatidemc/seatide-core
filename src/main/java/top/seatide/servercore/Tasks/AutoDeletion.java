package top.seatide.servercore.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import top.seatide.servercore.Main;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;
import top.seatide.servercore.Utils.Requests;

public class AutoDeletion {
    public static int maxEmpty = 9999999;
    public static String archiveScript = "";
    public static int currentEmpty = 0;
    public BukkitScheduler sche;
    public Runnable task;
    public Requests r;
    public static boolean locked = false;

    public void reload() {
        if (Files.cfg.getBoolean("saveCountdown")) {
            maxEmpty = Files.countdown.getInt("last-max-empty");
            currentEmpty = Files.countdown.getInt("last-empty-time");
            if (maxEmpty == 0) {
                maxEmpty = Files.cfg.getInt("maxEmptyTime");
            }
        } else {
            maxEmpty = Files.cfg.getInt("maxEmptyTime");
        }
        archiveScript = Files.cfg.getString("archiveScript");
        r = new Requests();
    }

    public AutoDeletion() {
        this.reload();
        sche = Bukkit.getServer().getScheduler();
        task = new Runnable() {
            @Override
            public void run() {
                if (locked) {
                    return;
                }
                int count = Bukkit.getOnlinePlayers().size();
                if (count == 0) {
                    currentEmpty += 1;
                }
                if (count > 0) {
                    currentEmpty = 0;
                }
                if (maxEmpty - currentEmpty < 60) {
                    LogUtil.info("实例将在 " + (maxEmpty - currentEmpty) + " 秒后释放。");
                }
                if (currentEmpty >= maxEmpty) {
                    if (!archiveScript.equals(null)) {
                        LogUtil.info("尝试运行归档脚本...");
                        try {
                            Process p = new ProcessBuilder(archiveScript).start();
                            locked = true;
                            int i = p.waitFor();
                            if (i == 0) {
                                LogUtil.success("归档脚本执行成功，返回码 0");
                                r.deleteInstance();
                            } else {
                                LogUtil.error("归档脚本执行失败，返回码 " + i);
                                currentEmpty = 0;
                                locked = false;
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.error("无法运行归档脚本，1 分钟后再试。");
                            currentEmpty = maxEmpty - 60;
                            locked = false;
                            return;
                        }
                    } else {
                        LogUtil.info("未检测到归档脚本，直接释放。");
                        r.deleteInstance();
                    }
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
