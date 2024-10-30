package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUserTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ValidationStatusUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ValidationStatusUser.class);
        ValidationStatusUser validationStatusUser1 = getValidationStatusUserSample1();
        ValidationStatusUser validationStatusUser2 = new ValidationStatusUser();
        assertThat(validationStatusUser1).isNotEqualTo(validationStatusUser2);

        validationStatusUser2.setId(validationStatusUser1.getId());
        assertThat(validationStatusUser1).isEqualTo(validationStatusUser2);

        validationStatusUser2 = getValidationStatusUserSample2();
        assertThat(validationStatusUser1).isNotEqualTo(validationStatusUser2);
    }

    @Test
    void appUserTest() {
        ValidationStatusUser validationStatusUser = getValidationStatusUserRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        validationStatusUser.setAppUser(appUserBack);
        assertThat(validationStatusUser.getAppUser()).isEqualTo(appUserBack);

        validationStatusUser.appUser(null);
        assertThat(validationStatusUser.getAppUser()).isNull();
    }
}
