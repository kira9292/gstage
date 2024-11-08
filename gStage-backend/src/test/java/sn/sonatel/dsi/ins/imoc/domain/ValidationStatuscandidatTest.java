package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidatTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ValidationStatuscandidatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ValidationStatuscandidat.class);
        ValidationStatuscandidat validationStatuscandidat1 = getValidationStatuscandidatSample1();
        ValidationStatuscandidat validationStatuscandidat2 = new ValidationStatuscandidat();
        assertThat(validationStatuscandidat1).isNotEqualTo(validationStatuscandidat2);

        validationStatuscandidat2.setId(validationStatuscandidat1.getId());
        assertThat(validationStatuscandidat1).isEqualTo(validationStatuscandidat2);

        validationStatuscandidat2 = getValidationStatuscandidatSample2();
        assertThat(validationStatuscandidat1).isNotEqualTo(validationStatuscandidat2);
    }

    @Test
    void candidatTest() {
        ValidationStatuscandidat validationStatuscandidat = getValidationStatuscandidatRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        validationStatuscandidat.setCandidat(candidatBack);
        assertThat(validationStatuscandidat.getCandidat()).isEqualTo(candidatBack);

        validationStatuscandidat.candidat(null);
        assertThat(validationStatuscandidat.getCandidat()).isNull();
    }
}
