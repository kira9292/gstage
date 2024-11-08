package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ValidationStatuscandidatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ValidationStatuscandidat getValidationStatuscandidatSample1() {
        return new ValidationStatuscandidat().id(1L).code("code1");
    }

    public static ValidationStatuscandidat getValidationStatuscandidatSample2() {
        return new ValidationStatuscandidat().id(2L).code("code2");
    }

    public static ValidationStatuscandidat getValidationStatuscandidatRandomSampleGenerator() {
        return new ValidationStatuscandidat().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString());
    }
}
