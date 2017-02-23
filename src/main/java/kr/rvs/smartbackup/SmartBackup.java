package kr.rvs.smartbackup;

import kr.rvs.smartbackup.abstraction.TimeUnit;
import kr.rvs.smartbackup.util.Static;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipOutputStream;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class SmartBackup extends JavaPlugin {
    // Key
    private static final String BACKUP_PERIOD = "backup-period";
    private static final String BACKUP_START_MESSAGE = "backup-start-message";
    private static final String BACKUP_FINISH_MESSAGE = "backup-finish-message";
    private static final String ESCAPE_FOLDERS = "escape-folders";
    private static final String ESCAPE_WORLDS = "escape-worlds";
    private static final String PATH = "path";

    // Default
    private static final String BACKUP_PERIOD_DEF = "1h30m12s";
    private static final String BACKUP_START_MESSAGE_DEF = "Backup will be started";
    private static final String BACKUP_FINISH_MESSAGE_DEF = "Backup finished";

    private final List<String> timeUnitList = new ArrayList<>(Arrays.asList("d", "h", "m", "s"));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        long period = getBackupPeriod();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            Bukkit.broadcastMessage(getStartMessage());
            File pluginsFolder = getDataFolder().getParentFile();
            File backupPath = getBackupPath();
            File[] files = pluginsFolder.listFiles();
            backupPath.getParentFile().mkdirs();

            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(getBackupPath()))) {
                if (files == null) {
                    return;
                }

                // Plugin data backup
                for (File file : files) {
                    if (!file.isDirectory()) {
                        continue;
                    }
                    String name = file.getName();
                    if (getEscapeFolders().contains(name)) {
                        continue;
                    }
                    Static.writeToZip(out, file);
                }

                // World backup
                for (World world : Bukkit.getWorlds()) {
                    if (getEscapeWorlds().contains(world.getName())) {
                        continue;
                    }
                    try {
                        Field worldServerField = world.getClass().getDeclaredField("world");
                        worldServerField.setAccessible(true);
                        Object worldServer = worldServerField.get(world);

                        Field dataManagerField = getSpecificSuperClass(worldServer.getClass(), "World").getDeclaredField("dataManager");
                        dataManagerField.setAccessible(true);
                        Object dataManager = dataManagerField.get(worldServer);

                        Field baseDirField = dataManager.getClass().getSuperclass().getDeclaredField("baseDir");
                        baseDirField.setAccessible(true);
                        File file = (File) baseDirField.get(dataManager);
                        Static.writeToZip(out, file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.broadcastMessage(getFinishMessage());
        }, period, period);
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

    private long getBackupPeriod() {
        String periodStr = getConfig().getString(BACKUP_PERIOD, BACKUP_PERIOD_DEF);
        if (isFlat(periodStr)) {
            return Long.valueOf(periodStr);
        }

        long tickPeriod = 0;
        for (TimeUnit unit : Static.stringParse(periodStr)) {
            tickPeriod += unit.toTick();
        }
        return tickPeriod;
    }

    private boolean isFlat(String periodStr) {
        for (String unit : timeUnitList) {
            if (periodStr.contains(unit)) {
                return false;
            }
        }
        return true;
    }

    private String getStartMessage() {
        return colorize(getConfig().getString(BACKUP_START_MESSAGE, BACKUP_START_MESSAGE_DEF));
    }

    private String getFinishMessage() {
        return colorize(getConfig().getString(BACKUP_FINISH_MESSAGE, BACKUP_FINISH_MESSAGE_DEF));
    }

    private List<String> getEscapeFolders() {
        return getConfig().getStringList(ESCAPE_FOLDERS);
    }

    private List<String> getEscapeWorlds() {
        return getConfig().getStringList(ESCAPE_WORLDS);
    }

    private File getBackupPath() {
        String path = getConfig().getString(PATH, "backups");
        return new File(path, getBackupFile());
    }

    private String getBackupFile() {
        String date = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        return "backup-" + date + ".zip";
    }

    private String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    private Class<?> getSpecificSuperClass(Class<?> cls, String name) {
        Class<?> superCls = cls.getSuperclass();
        if (superCls.getSimpleName().equals(name)) {
            return superCls;
        }
        return getSpecificSuperClass(superCls, name);
    }
}
