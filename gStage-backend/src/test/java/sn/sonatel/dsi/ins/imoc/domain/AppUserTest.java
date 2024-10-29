package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.RoleTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ServiceTestSamples.*;
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
    void serviceTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Service serviceBack = getServiceRandomSampleGenerator();

        appUser.setService(serviceBack);
        assertThat(appUser.getService()).isEqualTo(serviceBack);

        appUser.service(null);
        assertThat(appUser.getService()).isNull();
    }

    @Test
    void etatPaiementTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        EtatPaiement etatPaiementBack = getEtatPaiementRandomSampleGenerator();

        appUser.addEtatPaiement(etatPaiementBack);
        assertThat(appUser.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getAppUser()).isEqualTo(appUser);

        appUser.removeEtatPaiement(etatPaiementBack);
        assertThat(appUser.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getAppUser()).isNull();

        appUser.etatPaiements(new HashSet<>(Set.of(etatPaiementBack)));
        assertThat(appUser.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getAppUser()).isEqualTo(appUser);

        appUser.setEtatPaiements(new HashSet<>());
        assertThat(appUser.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getAppUser()).isNull();
    }

    @Test
    void contratTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        appUser.addContrat(contratBack);
        assertThat(appUser.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getAppUser()).isEqualTo(appUser);

        appUser.removeContrat(contratBack);
        assertThat(appUser.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getAppUser()).isNull();

        appUser.contrats(new HashSet<>(Set.of(contratBack)));
        assertThat(appUser.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getAppUser()).isEqualTo(appUser);

        appUser.setContrats(new HashSet<>());
        assertThat(appUser.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getAppUser()).isNull();
    }

    @Test
    void demandeStageTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        appUser.addDemandeStage(demandeStageBack);
        assertThat(appUser.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isEqualTo(appUser);

        appUser.removeDemandeStage(demandeStageBack);
        assertThat(appUser.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isNull();

        appUser.demandeStages(new HashSet<>(Set.of(demandeStageBack)));
        assertThat(appUser.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isEqualTo(appUser);

        appUser.setDemandeStages(new HashSet<>());
        assertThat(appUser.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isNull();
    }

    @Test
    void attestationPresenceTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        AttestationPresence attestationPresenceBack = getAttestationPresenceRandomSampleGenerator();

        appUser.addAttestationPresence(attestationPresenceBack);
        assertThat(appUser.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getAppUser()).isEqualTo(appUser);

        appUser.removeAttestationPresence(attestationPresenceBack);
        assertThat(appUser.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getAppUser()).isNull();

        appUser.attestationPresences(new HashSet<>(Set.of(attestationPresenceBack)));
        assertThat(appUser.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getAppUser()).isEqualTo(appUser);

        appUser.setAttestationPresences(new HashSet<>());
        assertThat(appUser.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getAppUser()).isNull();
    }

    @Test
    void candidatTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        appUser.addCandidat(candidatBack);
        assertThat(appUser.getCandidats()).containsOnly(candidatBack);
        assertThat(candidatBack.getAppUser()).isEqualTo(appUser);

        appUser.removeCandidat(candidatBack);
        assertThat(appUser.getCandidats()).doesNotContain(candidatBack);
        assertThat(candidatBack.getAppUser()).isNull();

        appUser.candidats(new HashSet<>(Set.of(candidatBack)));
        assertThat(appUser.getCandidats()).containsOnly(candidatBack);
        assertThat(candidatBack.getAppUser()).isEqualTo(appUser);

        appUser.setCandidats(new HashSet<>());
        assertThat(appUser.getCandidats()).doesNotContain(candidatBack);
        assertThat(candidatBack.getAppUser()).isNull();
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

    @Test
    void roleTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Role roleBack = getRoleRandomSampleGenerator();

        appUser.addRole(roleBack);
        assertThat(appUser.getRoles()).containsOnly(roleBack);

        appUser.removeRole(roleBack);
        assertThat(appUser.getRoles()).doesNotContain(roleBack);

        appUser.roles(new HashSet<>(Set.of(roleBack)));
        assertThat(appUser.getRoles()).containsOnly(roleBack);

        appUser.setRoles(new HashSet<>());
        assertThat(appUser.getRoles()).doesNotContain(roleBack);
    }
}
