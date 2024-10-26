package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DfcTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Dfc getDfcSample1() {
        return new Dfc().id(1L);
    }

    public static Dfc getDfcSample2() {
        return new Dfc().id(2L);
    }

    public static Dfc getDfcRandomSampleGenerator() {
        return new Dfc().id(longCount.incrementAndGet());
    }
}
