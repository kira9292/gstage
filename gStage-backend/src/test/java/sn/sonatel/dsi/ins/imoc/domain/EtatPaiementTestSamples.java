package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EtatPaiementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EtatPaiement getEtatPaiementSample1() {
        return new EtatPaiement()
            .id(1L)
            .reference("reference1")
            .paymentNumber("paymentNumber1")
            .actCode("actCode1")
            .paymentPhone("paymentPhone1")
            .comments("comments1");
    }

    public static EtatPaiement getEtatPaiementSample2() {
        return new EtatPaiement()
            .id(2L)
            .reference("reference2")
            .paymentNumber("paymentNumber2")
            .actCode("actCode2")
            .paymentPhone("paymentPhone2")
            .comments("comments2");
    }

    public static EtatPaiement getEtatPaiementRandomSampleGenerator() {
        return new EtatPaiement()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .paymentNumber(UUID.randomUUID().toString())
            .actCode(UUID.randomUUID().toString())
            .paymentPhone(UUID.randomUUID().toString())
            .comments(UUID.randomUUID().toString());
    }
}
