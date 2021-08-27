package top.seatide.servercore;

import java.util.Date;

import org.bukkit.plugin.java.JavaPlugin;

import top.seatide.servercore.Tasks.AutoDeletion;
import top.seatide.servercore.Utils.Files;
import top.seatide.servercore.Utils.LogUtil;

public final class Main extends JavaPlugin {
    public static AutoDeletion del;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Files.init(this);
        LogUtil.info("SEATiDE ServerCore 已启动");
        this.getCommand("seatidecore").setExecutor(new CommandHandler());
        del = new AutoDeletion();
        if (del.ready()) {
            del.run(this);
            LogUtil.success("自动停服机制已部署");
        } else {
            LogUtil.error("自动停服机制部署失败，请检查配置文件是否符合要求");
        }
    }

    @Override
    public void onDisable() {
        if (Files.cfg.getBoolean("saveCountdown")) {
            LogUtil.info("保存计时信息...");
            Files.countdown.set("last-empty-time", AutoDeletion.currentEmpty);
            Files.countdown.set("last-max-empty", AutoDeletion.maxEmpty);
            Files.countdown.set("updated", new Date().getTime());
            Files.save(Files.countdown, "./countdown.yml");
            LogUtil.success("保存成功。");
        }
        LogUtil.info("SEATiDE ServerCore 已停用");
    }
}
