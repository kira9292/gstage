package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BusinessUnitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BusinessUnit getBusinessUnitSample1() {
        return new BusinessUnit().id(1L).name("name1").description("description1").code("code1");
    }

    public static BusinessUnit getBusinessUnitSample2() {
        return new BusinessUnit().id(2L).name("name2").description("description2").code("code2");
    }

    public static BusinessUnit getBusinessUnitRandomSampleGenerator() {
        return new BusinessUnit()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString());
    }
}
