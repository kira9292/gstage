package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ValidationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Validation.class);
        Validation validation1 = getValidationSample1();
        Validation validation2 = new Validation();
        assertThat(validation1).isNotEqualTo(validation2);

        validation2.setId(validation1.getId());
        assertThat(validation1).isEqualTo(validation2);

        validation2 = getValidationSample2();
        assertThat(validation1).isNotEqualTo(validation2);
    }

    @Test
    void attestationPresenceTest() {
        Validation validation = getValidationRandomSampleGenerator();
        AttestationPresence attestationPresenceBack = getAttestationPresenceRandomSampleGenerator();

        validation.setAttestationPresence(attestationPresenceBack);
        assertThat(validation.getAttestationPresence()).isEqualTo(attestationPresenceBack);

        validation.attestationPresence(null);
        assertThat(validation.getAttestationPresence()).isNull();
    }

    @Test
    void contratTest() {
        Validation validation = getValidationRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        validation.setContrat(contratBack);
        assertThat(validation.getContrat()).isEqualTo(contratBack);

        validation.contrat(null);
        assertThat(validation.getContrat()).isNull();
    }

    @Test
    void attestationFinStageTest() {
        Validation validation = getValidationRandomSampleGenerator();
        AttestationFinStage attestationFinStageBack = getAttestationFinStageRandomSampleGenerator();

        validation.setAttestationFinStage(attestationFinStageBack);
        assertThat(validation.getAttestationFinStage()).isEqualTo(attestationFinStageBack);

        validation.attestationFinStage(null);
        assertThat(validation.getAttestationFinStage()).isNull();
    }

    @Test
    void userTest() {
        Validation validation = getValidationRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        validation.setUser(appUserBack);
        assertThat(validation.getUser()).isEqualTo(appUserBack);

        validation.user(null);
        assertThat(validation.getUser()).isNull();
    }
}
