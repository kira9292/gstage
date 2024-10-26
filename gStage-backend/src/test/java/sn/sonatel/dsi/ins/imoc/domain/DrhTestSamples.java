package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DrhTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Drh getDrhSample1() {
        return new Drh().id(1L).phone("phone1");
    }

    public static Drh getDrhSample2() {
        return new Drh().id(2L).phone("phone2");
    }

    public static Drh getDrhRandomSampleGenerator() {
        return new Drh().id(longCount.incrementAndGet()).phone(UUID.randomUUID().toString());
    }
}
