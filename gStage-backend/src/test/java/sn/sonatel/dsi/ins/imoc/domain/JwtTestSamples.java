package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JwtTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Jwt getJwtSample1() {
        return new Jwt().id(1L).valeur("valeur1");
    }

    public static Jwt getJwtSample2() {
        return new Jwt().id(2L).valeur("valeur2");
    }

    public static Jwt getJwtRandomSampleGenerator() {
        return new Jwt().id(longCount.incrementAndGet()).valeur(UUID.randomUUID().toString());
    }
}
