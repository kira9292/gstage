package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ManagerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Manager getManagerSample1() {
        return new Manager().id(1L).phone("phone1");
    }

    public static Manager getManagerSample2() {
        return new Manager().id(2L).phone("phone2");
    }

    public static Manager getManagerRandomSampleGenerator() {
        return new Manager().id(longCount.incrementAndGet()).phone(UUID.randomUUID().toString());
    }
}
