package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppServiceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ManagerTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ManagerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Manager.class);
        Manager manager1 = getManagerSample1();
        Manager manager2 = new Manager();
        assertThat(manager1).isNotEqualTo(manager2);

        manager2.setId(manager1.getId());
        assertThat(manager1).isEqualTo(manager2);

        manager2 = getManagerSample2();
        assertThat(manager1).isNotEqualTo(manager2);
    }

    @Test
    void serviceTest() {
        Manager manager = getManagerRandomSampleGenerator();
        AppService appServiceBack = getAppServiceRandomSampleGenerator();

        manager.setService(appServiceBack);
        assertThat(manager.getService()).isEqualTo(appServiceBack);

        manager.service(null);
        assertThat(manager.getService()).isNull();
    }

    @Test
    void attestationPresencesTest() {
        Manager manager = getManagerRandomSampleGenerator();
        AttestationPresence attestationPresenceBack = getAttestationPresenceRandomSampleGenerator();

        manager.addAttestationPresences(attestationPresenceBack);
        assertThat(manager.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getManager()).isEqualTo(manager);

        manager.removeAttestationPresences(attestationPresenceBack);
        assertThat(manager.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getManager()).isNull();

        manager.attestationPresences(new HashSet<>(Set.of(attestationPresenceBack)));
        assertThat(manager.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getManager()).isEqualTo(manager);

        manager.setAttestationPresences(new HashSet<>());
        assertThat(manager.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getManager()).isNull();
    }

    @Test
    void candidatsTest() {
        Manager manager = getManagerRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        manager.addCandidats(candidatBack);
        assertThat(manager.getCandidats()).containsOnly(candidatBack);
        assertThat(candidatBack.getManager()).isEqualTo(manager);

        manager.removeCandidats(candidatBack);
        assertThat(manager.getCandidats()).doesNotContain(candidatBack);
        assertThat(candidatBack.getManager()).isNull();

        manager.candidats(new HashSet<>(Set.of(candidatBack)));
        assertThat(manager.getCandidats()).containsOnly(candidatBack);
        assertThat(candidatBack.getManager()).isEqualTo(manager);

        manager.setCandidats(new HashSet<>());
        assertThat(manager.getCandidats()).doesNotContain(candidatBack);
        assertThat(candidatBack.getManager()).isNull();
    }

    @Test
    void demandeStagesTest() {
        Manager manager = getManagerRandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        manager.addDemandeStages(demandeStageBack);
        assertThat(manager.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getManager()).isEqualTo(manager);

        manager.removeDemandeStages(demandeStageBack);
        assertThat(manager.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getManager()).isNull();

        manager.demandeStages(new HashSet<>(Set.of(demandeStageBack)));
        assertThat(manager.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getManager()).isEqualTo(manager);

        manager.setDemandeStages(new HashSet<>());
        assertThat(manager.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getManager()).isNull();
    }
}
