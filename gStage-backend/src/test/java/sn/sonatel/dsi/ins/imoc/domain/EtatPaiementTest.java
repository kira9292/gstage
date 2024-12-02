package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;

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
    void candidatTest() {
        EtatPaiement etatPaiement = getEtatPaiementRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        etatPaiement.setCandidat(candidatBack);
        assertThat(etatPaiement.getCandidat()).isEqualTo(candidatBack);

        etatPaiement.candidat(null);
        assertThat(etatPaiement.getCandidat()).isNull();
    }
}
