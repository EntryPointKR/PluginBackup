package kr.rvs.smartbackup.abstraction;

/**
 * Created by Junhyeong Lim on 2017-02-23.
 */
public abstract class TimeUnit {
    protected final long value;

    public TimeUnit(long value) {
        this.value = value;
    }

    public abstract long toSecond();

    public final long toTick() {
        return toSecond() * 20;
    }

    public long getValue() {
        return value;
    }
}
