package top.seatide.servercore.Tasks;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import top.seatide.servercore.Main;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;

public class AutoBackup {
    public BukkitScheduler sche;
    public Runnable task;
    public static int timer = 0;
    public static int period = 0;
    public String backupScript = null;

    public void reload() {
        backupScript = Files.cfg.getString("backupScript");
        period = Files.cfg.getInt("backupPeriod");
    }

    public AutoBackup() {
        this.reload();
        sche = Bukkit.getServer().getScheduler();
        task = new Runnable(){
            @Override
            public void run() {
                if (timer >= period) {
                    LogUtil.info("尝试备份中...");
                    try {
                        var b = backup();
                        int i = b.waitFor();
                        if (i == 0) {
                            LogUtil.success("备份成功。");
                        } else {
                            LogUtil.error("备份脚本执行失败，返回码 " + i);
                        }
                    } catch (Exception e) {
                        LogUtil.error("备份脚本执行出现错误。");
                        e.printStackTrace();
                    }
                    timer = 0;
                } else {
                    timer += 1;
                }
            }
        };
    }

    public boolean ready() {
        return !this.backupScript.equals(null) && period >= 10;
    }

    public Process backup() throws IOException {
        return new ProcessBuilder(this.backupScript).start();
    }

    public void run(Main plugin) {
        sche.runTaskTimerAsynchronously(plugin, task, 0L, 20L);
    }
}