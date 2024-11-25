package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.NotificationTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void appUserTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        notification.setAppUser(appUserBack);
        assertThat(notification.getAppUser()).isEqualTo(appUserBack);

        notification.appUser(null);
        assertThat(notification.getAppUser()).isNull();
    }
}
