package top.seatide.servercore;

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
        LogUtil.info("SEATiDE ServerCore 已停用");
    }
}
