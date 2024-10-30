package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ValidationStatusUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ValidationStatusUser getValidationStatusUserSample1() {
        return new ValidationStatusUser().id(1L).code("code1");
    }

    public static ValidationStatusUser getValidationStatusUserSample2() {
        return new ValidationStatusUser().id(2L).code("code2");
    }

    public static ValidationStatusUser getValidationStatusUserRandomSampleGenerator() {
        return new ValidationStatusUser().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
