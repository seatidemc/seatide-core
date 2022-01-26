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
    public String archiveScript = null;
    public static int backupState = 0;

    public void reload() {
        backupScript = Files.cfg.getString("ecs.backupScript");
        archiveScript = Files.cfg.getString("ecs.archiveScript");
        period = Files.cfg.getInt("ecs.backupPeriod");
    }

    public AutoBackup() {
        this.reload();
        sche = Bukkit.getServer().getScheduler();
        task = new Runnable() {
            @Override
            public void run() {
                if (timer >= period) {
                    if (backupState == 0) {
                        doBackup();
                    }
                    timer = 0;
                } else {
                    timer += 1;
                }
            }
        };
    }

    public void doBackup() {
        backupState = 1;
        LogUtil.info("尝试备份中...");
        try {
            var b = backup();
            int i = b.waitFor();
            if (i == 0) {
                LogUtil.success("备份成功。");
                
            } else {
                LogUtil.error("备份脚本执行失败，返回码 " + i);
            }
            backupState = 0;
        } catch (Exception e) {
            LogUtil.error("备份脚本执行出现错误。");
            e.printStackTrace();
            backupState = 0;
        }
    }

    public void doArchive() {
        backupState = 1;
        LogUtil.info("尝试归档中...");
        try {
            var b = archive();
            int i = b.waitFor();
            if (i == 0) {
                LogUtil.success("归档成功。");
                
            } else {
                LogUtil.error("归档脚本执行失败，返回码 " + i);
            }
            backupState = 0;
        } catch (Exception e) {
            LogUtil.error("归档脚本执行出现错误。");
            e.printStackTrace();
            backupState = 0;
        }
    }

    public boolean ready() {
        return !this.backupScript.equals(null) && period >= 10;
    }

    public Process backup() throws IOException {
        return new ProcessBuilder(this.backupScript).start();
    }

    public Process archive() throws IOException {
        return new ProcessBuilder(this.archiveScript).start();
    }

    public void run(Main plugin) {
        sche.runTaskTimerAsynchronously(plugin, task, 0L, 20L);
    }
}
