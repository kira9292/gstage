package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DepartementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Departement getDepartementSample1() {
        return new Departement().id(1L).name("name1").description("description1");
    }

    public static Departement getDepartementSample2() {
        return new Departement().id(2L).name("name2").description("description2");
    }

    public static Departement getDepartementRandomSampleGenerator() {
        return new Departement()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
