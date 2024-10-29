package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;

import java.util.HashSet;
import java.util.Set;
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
    void contratsTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        candidat.addContrats(contratBack);
        assertThat(candidat.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getCandidat()).isEqualTo(candidat);

        candidat.removeContrats(contratBack);
        assertThat(candidat.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getCandidat()).isNull();

        candidat.contrats(new HashSet<>(Set.of(contratBack)));
        assertThat(candidat.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getCandidat()).isEqualTo(candidat);

        candidat.setContrats(new HashSet<>());
        assertThat(candidat.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getCandidat()).isNull();
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
    void appUserTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        candidat.setAppUser(appUserBack);
        assertThat(candidat.getAppUser()).isEqualTo(appUserBack);

        candidat.appUser(null);
        assertThat(candidat.getAppUser()).isNull();
    }
}
