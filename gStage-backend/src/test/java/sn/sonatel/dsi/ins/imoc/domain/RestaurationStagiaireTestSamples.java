package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RestaurationStagiaireTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RestaurationStagiaire getRestaurationStagiaireSample1() {
        return new RestaurationStagiaire().id(1L).cardNumber("cardNumber1");
    }

    public static RestaurationStagiaire getRestaurationStagiaireSample2() {
        return new RestaurationStagiaire().id(2L).cardNumber("cardNumber2");
    }

    public static RestaurationStagiaire getRestaurationStagiaireRandomSampleGenerator() {
        return new RestaurationStagiaire().id(longCount.incrementAndGet()).cardNumber(UUID.randomUUID().toString());
    }
}
