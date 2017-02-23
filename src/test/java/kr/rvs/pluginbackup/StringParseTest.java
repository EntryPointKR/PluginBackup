package kr.rvs.pluginbackup;

import kr.rvs.pluginbackup.abstraction.TimeUnit;
import kr.rvs.pluginbackup.util.Static;
import org.junit.Test;

import java.util.List;
import java.util.Random;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class StringParseTest {
    @Test
    public void randomStringParseTest() {
        Random random = new Random();
        long startTime = System.nanoTime();
        String testStr = Integer.toUnsignedLong(random.nextInt()) + "d"
                + nextInt(random, 24) + "h"
                + nextInt(random, 60) + "m"
                + nextInt(random, 60) + "s";
        List<TimeUnit> unitList = Static.stringParse(testStr);

        System.out.println("Test String: " + testStr);
        System.out.println("---------- Unit List ----------");

        for (TimeUnit unit : unitList) {
            String name = unit.getClass().getSimpleName();
            System.out.println(name + " Value: " + unit.getValue());
            System.out.println(name + " Second: " + unit.toSecond());
            System.out.println(name + " Tick: " + unit.toTick());
            System.out.println("------------------------------");
        }

        System.out.println("time: " + (System.nanoTime() - startTime));
    }

    private int nextInt(Random random, int bound) {
        return random.nextInt(bound) + 1;
    }
}
