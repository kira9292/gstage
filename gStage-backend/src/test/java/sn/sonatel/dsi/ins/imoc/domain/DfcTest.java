package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.DfcTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DfcTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Dfc.class);
        Dfc dfc1 = getDfcSample1();
        Dfc dfc2 = new Dfc();
        assertThat(dfc1).isNotEqualTo(dfc2);

        dfc2.setId(dfc1.getId());
        assertThat(dfc1).isEqualTo(dfc2);

        dfc2 = getDfcSample2();
        assertThat(dfc1).isNotEqualTo(dfc2);
    }

    @Test
    void hashCodeVerifier() {
        Dfc dfc = new Dfc();
        assertThat(dfc.hashCode()).isZero();

        Dfc dfc1 = getDfcSample1();
        dfc.setId(dfc1.getId());
        assertThat(dfc).hasSameHashCodeAs(dfc1);
    }

    @Test
    void etatPaiementsTest() {
        Dfc dfc = getDfcRandomSampleGenerator();
        EtatPaiement etatPaiementBack = getEtatPaiementRandomSampleGenerator();

        dfc.addEtatPaiements(etatPaiementBack);
        assertThat(dfc.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getDfc()).isEqualTo(dfc);

        dfc.removeEtatPaiements(etatPaiementBack);
        assertThat(dfc.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getDfc()).isNull();

        dfc.etatPaiements(new HashSet<>(Set.of(etatPaiementBack)));
        assertThat(dfc.getEtatPaiements()).containsOnly(etatPaiementBack);
        assertThat(etatPaiementBack.getDfc()).isEqualTo(dfc);

        dfc.setEtatPaiements(new HashSet<>());
        assertThat(dfc.getEtatPaiements()).doesNotContain(etatPaiementBack);
        assertThat(etatPaiementBack.getDfc()).isNull();
    }
}
