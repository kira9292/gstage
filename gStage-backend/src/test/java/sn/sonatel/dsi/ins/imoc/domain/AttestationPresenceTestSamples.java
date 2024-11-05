package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttestationPresenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AttestationPresence getAttestationPresenceSample1() {
        return new AttestationPresence().id(1L).comments("comments1");
    }

    public static AttestationPresence getAttestationPresenceSample2() {
        return new AttestationPresence().id(2L).comments("comments2");
    }

    public static AttestationPresence getAttestationPresenceRandomSampleGenerator() {
        return new AttestationPresence().id(longCount.incrementAndGet()).comments(UUID.randomUUID().toString());
    }
}
