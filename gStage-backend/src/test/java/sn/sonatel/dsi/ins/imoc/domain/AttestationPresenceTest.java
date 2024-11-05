package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AttestationPresenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttestationPresence.class);
        AttestationPresence attestationPresence1 = getAttestationPresenceSample1();
        AttestationPresence attestationPresence2 = new AttestationPresence();
        assertThat(attestationPresence1).isNotEqualTo(attestationPresence2);

        attestationPresence2.setId(attestationPresence1.getId());
        assertThat(attestationPresence1).isEqualTo(attestationPresence2);

        attestationPresence2 = getAttestationPresenceSample2();
        assertThat(attestationPresence1).isNotEqualTo(attestationPresence2);
    }

    @Test
    void validationsTest() {
        AttestationPresence attestationPresence = getAttestationPresenceRandomSampleGenerator();
        Validation validationBack = getValidationRandomSampleGenerator();

        attestationPresence.addValidations(validationBack);
        assertThat(attestationPresence.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getAttestationPresence()).isEqualTo(attestationPresence);

        attestationPresence.removeValidations(validationBack);
        assertThat(attestationPresence.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getAttestationPresence()).isNull();

        attestationPresence.validations(new HashSet<>(Set.of(validationBack)));
        assertThat(attestationPresence.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getAttestationPresence()).isEqualTo(attestationPresence);

        attestationPresence.setValidations(new HashSet<>());
        assertThat(attestationPresence.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getAttestationPresence()).isNull();
    }

    @Test
    void appUserTest() {
        AttestationPresence attestationPresence = getAttestationPresenceRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        attestationPresence.setAppUser(appUserBack);
        assertThat(attestationPresence.getAppUser()).isEqualTo(appUserBack);

        attestationPresence.appUser(null);
        assertThat(attestationPresence.getAppUser()).isNull();
    }
}
