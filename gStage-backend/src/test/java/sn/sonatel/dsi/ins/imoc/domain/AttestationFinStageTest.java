package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AttestationFinStageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttestationFinStage.class);
        AttestationFinStage attestationFinStage1 = getAttestationFinStageSample1();
        AttestationFinStage attestationFinStage2 = new AttestationFinStage();
        assertThat(attestationFinStage1).isNotEqualTo(attestationFinStage2);

        attestationFinStage2.setId(attestationFinStage1.getId());
        assertThat(attestationFinStage1).isEqualTo(attestationFinStage2);

        attestationFinStage2 = getAttestationFinStageSample2();
        assertThat(attestationFinStage1).isNotEqualTo(attestationFinStage2);
    }

    @Test
    void validationsTest() {
        AttestationFinStage attestationFinStage = getAttestationFinStageRandomSampleGenerator();
        Validation validationBack = getValidationRandomSampleGenerator();

        attestationFinStage.addValidations(validationBack);
        assertThat(attestationFinStage.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getAttestationFinStage()).isEqualTo(attestationFinStage);

        attestationFinStage.removeValidations(validationBack);
        assertThat(attestationFinStage.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getAttestationFinStage()).isNull();

        attestationFinStage.validations(new HashSet<>(Set.of(validationBack)));
        assertThat(attestationFinStage.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getAttestationFinStage()).isEqualTo(attestationFinStage);

        attestationFinStage.setValidations(new HashSet<>());
        assertThat(attestationFinStage.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getAttestationFinStage()).isNull();
    }

    @Test
    void contratTest() {
        AttestationFinStage attestationFinStage = getAttestationFinStageRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        attestationFinStage.setContrat(contratBack);
        assertThat(attestationFinStage.getContrat()).isEqualTo(contratBack);
        assertThat(contratBack.getAttestationFinStage()).isEqualTo(attestationFinStage);

        attestationFinStage.contrat(null);
        assertThat(attestationFinStage.getContrat()).isNull();
        assertThat(contratBack.getAttestationFinStage()).isNull();
    }
}
