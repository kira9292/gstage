package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssistantGWTETestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AssistantGWTE getAssistantGWTESample1() {
        return new AssistantGWTE().id(1L).phone("phone1");
    }

    public static AssistantGWTE getAssistantGWTESample2() {
        return new AssistantGWTE().id(2L).phone("phone2");
    }

    public static AssistantGWTE getAssistantGWTERandomSampleGenerator() {
        return new AssistantGWTE().id(longCount.incrementAndGet()).phone(UUID.randomUUID().toString());
    }
}
