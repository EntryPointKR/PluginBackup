package kr.rvs.smartbackup.util;

import kr.rvs.smartbackup.SmartBackup;
import kr.rvs.smartbackup.abstraction.TimeUnit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class Config {
    // Key
    private static final String BACKUP_PERIOD = "backup-period";
    private static final String BACKUP_START_MESSAGE = "backup-start-message";
    private static final String BACKUP_FINISH_MESSAGE = "backup-finish-message";
    private static final String ESCAPE_FOLDERS = "escape-folders";
    private static final String ESCAPE_WORLDS = "escape-worlds";
    private static final String BACKUP_LIMIT = "backup-limit";
    private static final String PATH = "path";

    // Default
    private static final String BACKUP_PERIOD_DEF = "1h30m12s";
    private static final String BACKUP_START_MESSAGE_DEF = "Backup will be started";
    private static final String BACKUP_FINISH_MESSAGE_DEF = "Backup finished";

    private static final List<String> timeUnitList = new ArrayList<>(Arrays.asList("d", "h", "m", "s"));

    public static long getBackupPeriod() {
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

    private static boolean isFlat(String periodStr) {
        for (String unit : timeUnitList) {
            if (periodStr.contains(unit)) {
                return false;
            }
        }
        return true;
    }

    public static String getStartMessage() {
        return Static.colorize(getConfig().getString(BACKUP_START_MESSAGE, BACKUP_START_MESSAGE_DEF));
    }

    public static String getFinishMessage() {
        return Static.colorize(getConfig().getString(BACKUP_FINISH_MESSAGE, BACKUP_FINISH_MESSAGE_DEF));
    }

    public static List<String> getEscapeFolders() {
        return getConfig().getStringList(ESCAPE_FOLDERS);
    }

    public static List<String> getEscapeWorlds() {
        return getConfig().getStringList(ESCAPE_WORLDS);
    }

    public static File getBackupPath() {
        String path = getConfig().getString(PATH, "backups");
        return new File(path, getBackupFile());
    }

    private static String getBackupFile() {
        String date = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
        return "backup-" + date + ".zip";
    }

    public static int getBackupLimit() {
        return getConfig().getInt(BACKUP_LIMIT, 50);
    }

    private static FileConfiguration getConfig() {
        return SmartBackup.inst.getConfig();
    }
}
