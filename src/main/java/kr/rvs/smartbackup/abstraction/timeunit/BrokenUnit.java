package kr.rvs.smartbackup.abstraction.timeunit;

import kr.rvs.smartbackup.abstraction.TimeUnit;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public class BrokenUnit extends TimeUnit {
    public BrokenUnit() {
        super(0);
    }

    @Override
    public long toSecond() {
        return 0;
    }
}
