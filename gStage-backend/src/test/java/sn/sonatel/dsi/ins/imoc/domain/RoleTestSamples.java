package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RoleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Role getRoleSample1() {
        return new Role().id(1L);
    }

    public static Role getRoleSample2() {
        return new Role().id(2L);
    }

    public static Role getRoleRandomSampleGenerator() {
        return new Role().id(longCount.incrementAndGet());
    }
}
