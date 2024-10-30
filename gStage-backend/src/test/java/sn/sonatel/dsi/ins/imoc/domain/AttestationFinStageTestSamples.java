package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AttestationFinStageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AttestationFinStage getAttestationFinStageSample1() {
        return new AttestationFinStage().id(1L).reference("reference1").comments("comments1");
    }

    public static AttestationFinStage getAttestationFinStageSample2() {
        return new AttestationFinStage().id(2L).reference("reference2").comments("comments2");
    }

    public static AttestationFinStage getAttestationFinStageRandomSampleGenerator() {
        return new AttestationFinStage()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}
