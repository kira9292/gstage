package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AssistantGWTETestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DfcTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class EtatPaiementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtatPaiement.class);
        EtatPaiement etatPaiement1 = getEtatPaiementSample1();
        EtatPaiement etatPaiement2 = new EtatPaiement();
        assertThat(etatPaiement1).isNotEqualTo(etatPaiement2);

        etatPaiement2.setId(etatPaiement1.getId());
        assertThat(etatPaiement1).isEqualTo(etatPaiement2);

        etatPaiement2 = getEtatPaiementSample2();
        assertThat(etatPaiement1).isNotEqualTo(etatPaiement2);
    }

    @Test
    void attestationPresencesTest() {
        EtatPaiement etatPaiement = getEtatPaiementRandomSampleGenerator();
        AttestationPresence attestationPresenceBack = getAttestationPresenceRandomSampleGenerator();

        etatPaiement.addAttestationPresences(attestationPresenceBack);
        assertThat(etatPaiement.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getEtatPaiement()).isEqualTo(etatPaiement);

        etatPaiement.removeAttestationPresences(attestationPresenceBack);
        assertThat(etatPaiement.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getEtatPaiement()).isNull();

        etatPaiement.attestationPresences(new HashSet<>(Set.of(attestationPresenceBack)));
        assertThat(etatPaiement.getAttestationPresences()).containsOnly(attestationPresenceBack);
        assertThat(attestationPresenceBack.getEtatPaiement()).isEqualTo(etatPaiement);

        etatPaiement.setAttestationPresences(new HashSet<>());
        assertThat(etatPaiement.getAttestationPresences()).doesNotContain(attestationPresenceBack);
        assertThat(attestationPresenceBack.getEtatPaiement()).isNull();
    }

    @Test
    void contratTest() {
        EtatPaiement etatPaiement = getEtatPaiementRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        etatPaiement.setContrat(contratBack);
        assertThat(etatPaiement.getContrat()).isEqualTo(contratBack);

        etatPaiement.contrat(null);
        assertThat(etatPaiement.getContrat()).isNull();
    }

    @Test
    void dfcTest() {
        EtatPaiement etatPaiement = getEtatPaiementRandomSampleGenerator();
        Dfc dfcBack = getDfcRandomSampleGenerator();

        etatPaiement.setDfc(dfcBack);
        assertThat(etatPaiement.getDfc()).isEqualTo(dfcBack);

        etatPaiement.dfc(null);
        assertThat(etatPaiement.getDfc()).isNull();
    }

    @Test
    void assistantGWTECreatorTest() {
        EtatPaiement etatPaiement = getEtatPaiementRandomSampleGenerator();
        AssistantGWTE assistantGWTEBack = getAssistantGWTERandomSampleGenerator();

        etatPaiement.setAssistantGWTECreator(assistantGWTEBack);
        assertThat(etatPaiement.getAssistantGWTECreator()).isEqualTo(assistantGWTEBack);

        etatPaiement.assistantGWTECreator(null);
        assertThat(etatPaiement.getAssistantGWTECreator()).isNull();
    }
}
