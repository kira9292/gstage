package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void validationsTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Validation validationBack = getValidationRandomSampleGenerator();

        appUser.addValidations(validationBack);
        assertThat(appUser.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getUser()).isEqualTo(appUser);

        appUser.removeValidations(validationBack);
        assertThat(appUser.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getUser()).isNull();

        appUser.validations(new HashSet<>(Set.of(validationBack)));
        assertThat(appUser.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getUser()).isEqualTo(appUser);

        appUser.setValidations(new HashSet<>());
        assertThat(appUser.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getUser()).isNull();
    }
}
