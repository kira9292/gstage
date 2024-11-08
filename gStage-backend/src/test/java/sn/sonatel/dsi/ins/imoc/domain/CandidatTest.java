package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidatTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class CandidatTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Candidat.class);
        Candidat candidat1 = getCandidatSample1();
        Candidat candidat2 = new Candidat();
        assertThat(candidat1).isNotEqualTo(candidat2);

        candidat2.setId(candidat1.getId());
        assertThat(candidat1).isEqualTo(candidat2);

        candidat2 = getCandidatSample2();
        assertThat(candidat1).isNotEqualTo(candidat2);
    }

    @Test
    void demandeStageTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        candidat.setDemandeStage(demandeStageBack);
        assertThat(candidat.getDemandeStage()).isEqualTo(demandeStageBack);
        assertThat(demandeStageBack.getCandidat()).isEqualTo(candidat);

        candidat.demandeStage(null);
        assertThat(candidat.getDemandeStage()).isNull();
        assertThat(demandeStageBack.getCandidat()).isNull();
    }

    @Test
    void validationStatuscandidatTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        ValidationStatuscandidat validationStatuscandidatBack = getValidationStatuscandidatRandomSampleGenerator();

        candidat.setValidationStatuscandidat(validationStatuscandidatBack);
        assertThat(candidat.getValidationStatuscandidat()).isEqualTo(validationStatuscandidatBack);
        assertThat(validationStatuscandidatBack.getCandidat()).isEqualTo(candidat);

        candidat.validationStatuscandidat(null);
        assertThat(candidat.getValidationStatuscandidat()).isNull();
        assertThat(validationStatuscandidatBack.getCandidat()).isNull();
    }

    @Test
    void appUserTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        candidat.setAppUser(appUserBack);
        assertThat(candidat.getAppUser()).isEqualTo(appUserBack);

        candidat.appUser(null);
        assertThat(candidat.getAppUser()).isNull();
    }
}
