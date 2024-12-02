package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatuscandidatTestSamples.*;

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
    void attestationFinStageTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        AttestationFinStage attestationFinStageBack = getAttestationFinStageRandomSampleGenerator();

        candidat.setAttestationFinStage(attestationFinStageBack);
        assertThat(candidat.getAttestationFinStage()).isEqualTo(attestationFinStageBack);

        candidat.attestationFinStage(null);
        assertThat(candidat.getAttestationFinStage()).isNull();
    }

    @Test
    void etatPaiementTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        EtatPaiement etatPaiementBack = getEtatPaiementRandomSampleGenerator();

        candidat.addEtatPaiement(etatPaiementBack);
        assertThat(candidat.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getCandidat()).isEqualTo(candidat);

        candidat.removeEtatPaiement(etatPaiementBack);
        assertThat(candidat.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getCandidat()).isNull();

        candidat.etatPaiements(new HashSet<>(Set.of(etatPaiementBack)));
        assertThat(candidat.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getCandidat()).isEqualTo(candidat);

        candidat.setEtatPaiements(new HashSet<>());
        assertThat(candidat.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getCandidat()).isNull();
    }

    @Test
    void contratTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        candidat.addContrat(contratBack);
        assertThat(candidat.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getCandidat()).isEqualTo(candidat);

        candidat.removeContrat(contratBack);
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
    void attestationPresenceTest() {
        Candidat candidat = getCandidatRandomSampleGenerator();
        AttestationPresence attestationPresenceBack = getAttestationPresenceRandomSampleGenerator();

        candidat.addAttestationPresence(attestationPresenceBack);
        assertThat(candidat.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getCandidat()).isEqualTo(candidat);

        candidat.removeAttestationPresence(attestationPresenceBack);
        assertThat(candidat.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getCandidat()).isNull();

        candidat.attestationPresences(new HashSet<>(Set.of(attestationPresenceBack)));
        assertThat(candidat.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getCandidat()).isEqualTo(candidat);

        candidat.setAttestationPresences(new HashSet<>());
        assertThat(candidat.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getCandidat()).isNull();
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
