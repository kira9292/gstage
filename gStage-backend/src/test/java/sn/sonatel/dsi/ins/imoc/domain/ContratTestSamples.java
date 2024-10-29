package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContratTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Contrat getContratSample1() {
        return new Contrat().id(1L).reference("reference1").assignmentSite("assignmentSite1").comments("comments1");
    }

    public static Contrat getContratSample2() {
        return new Contrat().id(2L).reference("reference2").assignmentSite("assignmentSite2").comments("comments2");
    }

    public static Contrat getContratRandomSampleGenerator() {
        return new Contrat()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .assignmentSite(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}
