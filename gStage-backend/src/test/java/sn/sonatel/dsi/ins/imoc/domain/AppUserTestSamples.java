package sn.sonatel.dsi.ins.imoc.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AppUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AppUser getAppUserSample1() {
        return new AppUser()
            .id(1L)
            .username("username1")
            .email("email1")
            .password("password1")
            .name("name1")
            .firstName("firstName1")
            .phone("phone1")
            .formation("formation1");
    }

    public static AppUser getAppUserSample2() {
        return new AppUser()
            .id(2L)
            .username("username2")
            .email("email2")
            .password("password2")
            .name("name2")
            .firstName("firstName2")
            .phone("phone2")
            .formation("formation2");
    }

    public static AppUser getAppUserRandomSampleGenerator() {
        return new AppUser()
            .id(longCount.incrementAndGet())
            .username(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .formation(UUID.randomUUID().toString());
    }
}
