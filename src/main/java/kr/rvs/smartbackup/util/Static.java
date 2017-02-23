package kr.rvs.smartbackup.util;

import kr.rvs.smartbackup.abstraction.TimeUnit;
import kr.rvs.smartbackup.abstraction.timeunit.BrokenUnit;
import kr.rvs.smartbackup.abstraction.timeunit.DayUnit;
import kr.rvs.smartbackup.abstraction.timeunit.HourUnit;
import kr.rvs.smartbackup.abstraction.timeunit.MinuteUnit;
import kr.rvs.smartbackup.abstraction.timeunit.SecondUnit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class Static {
    public static List<TimeUnit> stringParse(String periodStr) {
        List<TimeUnit> ret = new ArrayList<>();
        char[] chars = periodStr.toCharArray();
        char[] tempChars = new char[chars.length];
        int tempIndex = 0;

        for (char ch : chars) {
            Class<? extends TimeUnit> cls = null;

            if (ch == 'd') {
                cls = DayUnit.class;
            } else if (ch == 'h') {
                cls = HourUnit.class;
            } else if (ch == 'm') {
                cls = MinuteUnit.class;
            } else if (ch == 's') {
                cls = SecondUnit.class;
            }

            if (cls != null) {
                tempChars = addTimeUnit(ret, cls, tempChars, tempIndex);
                tempIndex = 0;
            } else {
                tempChars[tempIndex++] = ch;
            }
        }

        return ret;
    }

    private static char[] addTimeUnit(List<TimeUnit> unitList, Class<? extends TimeUnit> cls, char[] chars, int index) {
        unitList.add(ensure(cls, chars, index));
        return new char[chars.length];
    }

    private static TimeUnit ensure(Class<? extends TimeUnit> cls, char[] chars, int index) {
        char[] newChars = new char[index];
        System.arraycopy(chars, 0, newChars, 0, index);
        try {
            return cls.getConstructor(long.class).newInstance(Long.valueOf(new String(newChars)));
        } catch (Exception ex) {
            return new BrokenUnit();
        }
    }

    public static void writeToZip(ZipOutputStream out, File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                writeToZip(out, file);
            } else {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
                out.putNextEntry(new JarEntry(file.getPath()));
                out.write(getBytesFromStream(in));
                out.closeEntry();
                in.close();
            }
        }
    }

    public static byte[] getBytesFromStream(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read;
        byte[] bytes = new byte[65536];
        while ((read = in.read(bytes, 0, bytes.length)) != -1) {
            out.write(bytes, 0, read);
        }
        return out.toByteArray();
    }
}
