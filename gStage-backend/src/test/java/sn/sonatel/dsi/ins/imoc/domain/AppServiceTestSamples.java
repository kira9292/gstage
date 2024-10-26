package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppServiceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppService getAppServiceSample1() {
        return new AppService().id(1L).name("name1").description("description1");
    }

    public static AppService getAppServiceSample2() {
        return new AppService().id(2L).name("name2").description("description2");
    }

    public static AppService getAppServiceRandomSampleGenerator() {
        return new AppService()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
