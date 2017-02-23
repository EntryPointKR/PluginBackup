package kr.rvs.smartbackup.abstraction.timeunit;

import kr.rvs.smartbackup.abstraction.TimeUnit;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class DayUnit extends TimeUnit {
    public DayUnit(long amount) {
        super(amount);
    }

    @Override
    public long toSecond() {
        return value * 24 * (60 * 60);
    }
}
