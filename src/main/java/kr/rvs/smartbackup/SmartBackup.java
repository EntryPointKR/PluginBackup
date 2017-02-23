package kr.rvs.smartbackup;

import kr.rvs.smartbackup.abstraction.BackupRunnable;
import kr.rvs.smartbackup.util.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class SmartBackup extends JavaPlugin {
    public static Plugin inst;

    public SmartBackup() {
        inst = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        long period = Config.getBackupPeriod();
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                this, new BackupRunnable(), period, period);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("Permission denied.");
        } else {
            reloadConfig();
            sender.sendMessage("Configuration reloaded.");
        }
        return true;
    }
}
