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
        FileAndAmountStorage storage = fileCounting(backupPath.getParentFile());

        backupPath.getParentFile().mkdirs();

        if (storage.count >= Config.getBackupLimit()
                && storage.file != null) {
            if (!storage.file.delete()) {
                return;
            }
        }

        // Start
        Bukkit.broadcastMessage(Config.getStartMessage());
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(backupPath))) {
            if (files == null) {
                return;
            }

            pluginDataBackup(out, files);
            worldBackup(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.broadcastMessage(Config.getFinishMessage());
    }

    private FileAndAmountStorage fileCounting(File backupFolder) {
        File ret = null;
        long modified = System.currentTimeMillis();
        File[] listFiles = backupFolder.listFiles();
        int count = 0;

        if (listFiles == null) {
            return new FileAndAmountStorage(null, 0);
        }

        for (File file : listFiles) {
            if (!isBackupFile(file))
                continue;
            long lastModified = file.lastModified();
            if (modified > lastModified) {
                ret = file;
                modified = lastModified;
            }
            count++;
        }

        return new FileAndAmountStorage(ret, count);
    }

    private void pluginDataBackup(ZipOutputStream out, File[] files) throws IOException {
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
    }

    private void worldBackup(ZipOutputStream out) {
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
    }

    private boolean isBackupFile(File file) {
        String name = file.getName();
        return name.startsWith("backup-") && name.endsWith(".zip");
    }

    class FileAndAmountStorage {
        private File file;
        private int count;

        public FileAndAmountStorage(File file, int count) {
            this.file = file;
            this.count = count;
        }
    }
}
