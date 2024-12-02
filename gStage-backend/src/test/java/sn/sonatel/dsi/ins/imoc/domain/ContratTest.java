package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ContratTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Contrat.class);
        Contrat contrat1 = getContratSample1();
        Contrat contrat2 = new Contrat();
        assertThat(contrat1).isNotEqualTo(contrat2);

        contrat2.setId(contrat1.getId());
        assertThat(contrat1).isEqualTo(contrat2);

        contrat2 = getContratSample2();
        assertThat(contrat1).isNotEqualTo(contrat2);
    }

    @Test
    void validationsTest() {
        Contrat contrat = getContratRandomSampleGenerator();
        Validation validationBack = getValidationRandomSampleGenerator();

        contrat.addValidations(validationBack);
        assertThat(contrat.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getContrat()).isEqualTo(contrat);

        contrat.removeValidations(validationBack);
        assertThat(contrat.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getContrat()).isNull();

        contrat.validations(new HashSet<>(Set.of(validationBack)));
        assertThat(contrat.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getContrat()).isEqualTo(contrat);

        contrat.setValidations(new HashSet<>());
        assertThat(contrat.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getContrat()).isNull();
    }

    @Test
    void candidatTest() {
        Contrat contrat = getContratRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        contrat.setCandidat(candidatBack);
        assertThat(contrat.getCandidat()).isEqualTo(candidatBack);

        contrat.candidat(null);
        assertThat(contrat.getCandidat()).isNull();
    }
}
