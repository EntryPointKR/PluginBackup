package kr.rvs.smartbackup.abstraction;

import kr.rvs.smartbackup.SmartBackup;
import kr.rvs.smartbackup.util.Config;
import kr.rvs.smartbackup.util.Static;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.zip.ZipOutputStream;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class BackupRunnable implements Runnable {
    @Override
    public void run() {
        File pluginsFolder = SmartBackup.inst.getDataFolder().getParentFile();
        File backupPath = Config.getBackupPath();
        File[] files = pluginsFolder.listFiles();
        File[] backupFiles = backupPath.listFiles();
        backupPath.getParentFile().mkdirs();

        int backupCount = 0;

        // Backup file counting
        if (backupFiles != null) {
            for (File backupFile : backupFiles) {
                String name = backupFile.getName();
                if (name.contains("backup-")
                        && name.endsWith(".zip")) {
                    backupCount++;
                }
            }
        }

        // Limit
        if (backupCount > Config.getBackupLimit()) {
            return;
        }

        // Start
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(backupPath))) {
            if (files == null) {
                return;
            }

            Bukkit.broadcastMessage(Config.getStartMessage());

            // Plugin data backup
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                String name = file.getName();
                if (Config.getEscapeFolders().contains(name)) {
                    continue;
                }
                Static.writeToZip(out, file);
            }

            // World backup
            for (World world : Bukkit.getWorlds()) {
                if (Config.getEscapeWorlds().contains(world.getName())) {
                    continue;
                }
                try {
                    Field worldServerField = world.getClass().getDeclaredField("world");
                    worldServerField.setAccessible(true);
                    Object worldServer = worldServerField.get(world);

                    Field dataManagerField = Static.getSpecificSuperClass(worldServer.getClass(), "World").getDeclaredField("dataManager");
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
        Bukkit.broadcastMessage(Config.getFinishMessage());
    }
}
