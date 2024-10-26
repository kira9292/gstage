package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AssistantGWTETestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AssistantGWTETest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssistantGWTE.class);
        AssistantGWTE assistantGWTE1 = getAssistantGWTESample1();
        AssistantGWTE assistantGWTE2 = new AssistantGWTE();
        assertThat(assistantGWTE1).isNotEqualTo(assistantGWTE2);

        assistantGWTE2.setId(assistantGWTE1.getId());
        assertThat(assistantGWTE1).isEqualTo(assistantGWTE2);

        assistantGWTE2 = getAssistantGWTESample2();
        assertThat(assistantGWTE1).isNotEqualTo(assistantGWTE2);
    }

    @Test
    void demandeStagesTest() {
        AssistantGWTE assistantGWTE = getAssistantGWTERandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        assistantGWTE.addDemandeStages(demandeStageBack);
        assertThat(assistantGWTE.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getAssistantGWTE()).isEqualTo(assistantGWTE);

        assistantGWTE.removeDemandeStages(demandeStageBack);
        assertThat(assistantGWTE.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getAssistantGWTE()).isNull();

        assistantGWTE.demandeStages(new HashSet<>(Set.of(demandeStageBack)));
        assertThat(assistantGWTE.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getAssistantGWTE()).isEqualTo(assistantGWTE);

        assistantGWTE.setDemandeStages(new HashSet<>());
        assertThat(assistantGWTE.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getAssistantGWTE()).isNull();
    }

    @Test
    void contratsTest() {
        AssistantGWTE assistantGWTE = getAssistantGWTERandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        assistantGWTE.addContrats(contratBack);
        assertThat(assistantGWTE.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getAssistantGWTECreator()).isEqualTo(assistantGWTE);

        assistantGWTE.removeContrats(contratBack);
        assertThat(assistantGWTE.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getAssistantGWTECreator()).isNull();

        assistantGWTE.contrats(new HashSet<>(Set.of(contratBack)));
        assertThat(assistantGWTE.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getAssistantGWTECreator()).isEqualTo(assistantGWTE);

        assistantGWTE.setContrats(new HashSet<>());
        assertThat(assistantGWTE.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getAssistantGWTECreator()).isNull();
    }

    @Test
    void etatPaiementsTest() {
        AssistantGWTE assistantGWTE = getAssistantGWTERandomSampleGenerator();
        EtatPaiement etatPaiementBack = getEtatPaiementRandomSampleGenerator();

        assistantGWTE.addEtatPaiements(etatPaiementBack);
        assertThat(assistantGWTE.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getAssistantGWTECreator()).isEqualTo(assistantGWTE);

        assistantGWTE.removeEtatPaiements(etatPaiementBack);
        assertThat(assistantGWTE.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getAssistantGWTECreator()).isNull();

        assistantGWTE.etatPaiements(new HashSet<>(Set.of(etatPaiementBack)));
        assertThat(assistantGWTE.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getAssistantGWTECreator()).isEqualTo(assistantGWTE);

        assistantGWTE.setEtatPaiements(new HashSet<>());
        assertThat(assistantGWTE.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getAssistantGWTECreator()).isNull();
    }
}
