package kr.rvs.pluginbackup.abstraction.timeunit;

import kr.rvs.pluginbackup.abstraction.TimeUnit;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class SecondUnit extends TimeUnit {
    public SecondUnit(long amount) {
        super(amount);
    }

    @Override
    public long toSecond() {
        return value;
    }
}
