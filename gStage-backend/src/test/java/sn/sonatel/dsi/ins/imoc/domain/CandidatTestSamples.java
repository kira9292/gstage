package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CandidatTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Candidat getCandidatSample1() {
        return new Candidat()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .nationality("nationality1")
            .birthPlace("birthPlace1")
            .cni("cni1")
            .address("address1")
            .email("email1")
            .phone("phone1")
            .school("school1");
    }

    public static Candidat getCandidatSample2() {
        return new Candidat()
            .id(2L)
            .firstName("firstName2")
            .lastName("lastName2")
            .nationality("nationality2")
            .birthPlace("birthPlace2")
            .cni("cni2")
            .address("address2")
            .email("email2")
            .phone("phone2")
            .school("school2");
    }

    public static Candidat getCandidatRandomSampleGenerator() {
        return new Candidat()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .nationality(UUID.randomUUID().toString())
            .birthPlace(UUID.randomUUID().toString())
            .cni(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .school(UUID.randomUUID().toString());
    }
}
