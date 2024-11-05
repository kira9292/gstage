package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ValidationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Validation getValidationSample1() {
        return new Validation().id(1L).comments("comments1").validatedBy("validatedBy1");
    }

    public static Validation getValidationSample2() {
        return new Validation().id(2L).comments("comments2").validatedBy("validatedBy2");
    }

    public static Validation getValidationRandomSampleGenerator() {
        return new Validation()
            .id(longCount.incrementAndGet())
            .comments(UUID.randomUUID().toString())
            .validatedBy(UUID.randomUUID().toString());
    }
}
