package kr.rvs.pluginbackup.abstraction.timeunit;

import kr.rvs.pluginbackup.abstraction.TimeUnit;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class MinuteUnit extends TimeUnit {
    public MinuteUnit(long amount) {
        super(amount);
    }

    @Override
    public long toSecond() {
        return value * 60;
    }
}
