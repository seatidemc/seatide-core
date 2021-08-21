package top.seatide.servercore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import top.seatide.servercore.Tasks.AutoDeletion;
import top.seatide.servercore.Utils.LogUtil;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        LogUtil.info("SEATiDE ServerCore 已启动");
        var cfg = this.getConfig();
        this.initAutoDeletion(cfg);
    }

    @Override
    public void onDisable() {
        LogUtil.info("SEATiDE ServerCore 已停用");
    }

    public void initAutoDeletion(FileConfiguration cfg) {
        var maxEmpty = cfg.getInt("maxEmptyTime");
        var site = cfg.getString("site");
        if (maxEmpty > 10 || site.equals(null)) {
            var backupScript = cfg.getString("backupScript");
            var del = new AutoDeletion(maxEmpty, backupScript, site);
            del.run(this);
            LogUtil.success("自动停服机制已部署");
        } else {
            LogUtil.error("无法部署自动停服机制，请检查配置文件");
        }
    }
}
