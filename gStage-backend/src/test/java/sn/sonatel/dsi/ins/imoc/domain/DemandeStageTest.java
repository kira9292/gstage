package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AssistantGWTETestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DepartementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ManagerTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DemandeStageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DemandeStage.class);
        DemandeStage demandeStage1 = getDemandeStageSample1();
        DemandeStage demandeStage2 = new DemandeStage();
        assertThat(demandeStage1).isNotEqualTo(demandeStage2);

        demandeStage2.setId(demandeStage1.getId());
        assertThat(demandeStage1).isEqualTo(demandeStage2);

        demandeStage2 = getDemandeStageSample2();
        assertThat(demandeStage1).isNotEqualTo(demandeStage2);
    }

    @Test
    void candidatTest() {
        DemandeStage demandeStage = getDemandeStageRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        demandeStage.setCandidat(candidatBack);
        assertThat(demandeStage.getCandidat()).isEqualTo(candidatBack);

        demandeStage.candidat(null);
        assertThat(demandeStage.getCandidat()).isNull();
    }

    @Test
    void assistantGWTETest() {
        DemandeStage demandeStage = getDemandeStageRandomSampleGenerator();
        AssistantGWTE assistantGWTEBack = getAssistantGWTERandomSampleGenerator();

        demandeStage.setAssistantGWTE(assistantGWTEBack);
        assertThat(demandeStage.getAssistantGWTE()).isEqualTo(assistantGWTEBack);

        demandeStage.assistantGWTE(null);
        assertThat(demandeStage.getAssistantGWTE()).isNull();
    }

    @Test
    void managerTest() {
        DemandeStage demandeStage = getDemandeStageRandomSampleGenerator();
        Manager managerBack = getManagerRandomSampleGenerator();

        demandeStage.setManager(managerBack);
        assertThat(demandeStage.getManager()).isEqualTo(managerBack);

        demandeStage.manager(null);
        assertThat(demandeStage.getManager()).isNull();
    }

    @Test
    void departementTest() {
        DemandeStage demandeStage = getDemandeStageRandomSampleGenerator();
        Departement departementBack = getDepartementRandomSampleGenerator();

        demandeStage.setDepartement(departementBack);
        assertThat(demandeStage.getDepartement()).isEqualTo(departementBack);

        demandeStage.departement(null);
        assertThat(demandeStage.getDepartement()).isNull();
    }

    @Test
    void businessUnitTest() {
        DemandeStage demandeStage = getDemandeStageRandomSampleGenerator();
        BusinessUnit businessUnitBack = getBusinessUnitRandomSampleGenerator();

        demandeStage.setBusinessUnit(businessUnitBack);
        assertThat(demandeStage.getBusinessUnit()).isEqualTo(businessUnitBack);

        demandeStage.businessUnit(null);
        assertThat(demandeStage.getBusinessUnit()).isNull();
    }
}
