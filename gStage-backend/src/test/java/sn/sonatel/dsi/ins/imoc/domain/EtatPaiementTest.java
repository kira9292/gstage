package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
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
    void appUserTest() {
        EtatPaiement etatPaiement = getEtatPaiementRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        etatPaiement.setAppUser(appUserBack);
        assertThat(etatPaiement.getAppUser()).isEqualTo(appUserBack);

        etatPaiement.appUser(null);
        assertThat(etatPaiement.getAppUser()).isNull();
    }
}
