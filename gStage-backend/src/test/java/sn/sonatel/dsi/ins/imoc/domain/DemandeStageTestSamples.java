package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class DemandeStageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static DemandeStage getDemandeStageSample1() {
        return new DemandeStage().id(1L);
    }

    public static DemandeStage getDemandeStageSample2() {
        return new DemandeStage().id(2L);
    }

    public static DemandeStage getDemandeStageRandomSampleGenerator() {
        return new DemandeStage().id(longCount.incrementAndGet());
    }
}
