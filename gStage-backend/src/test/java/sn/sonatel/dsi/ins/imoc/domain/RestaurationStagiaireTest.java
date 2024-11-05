package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaireTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class RestaurationStagiaireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurationStagiaire.class);
        RestaurationStagiaire restaurationStagiaire1 = getRestaurationStagiaireSample1();
        RestaurationStagiaire restaurationStagiaire2 = new RestaurationStagiaire();
        assertThat(restaurationStagiaire1).isNotEqualTo(restaurationStagiaire2);

        restaurationStagiaire2.setId(restaurationStagiaire1.getId());
        assertThat(restaurationStagiaire1).isEqualTo(restaurationStagiaire2);

        restaurationStagiaire2 = getRestaurationStagiaireSample2();
        assertThat(restaurationStagiaire1).isNotEqualTo(restaurationStagiaire2);
    }

    @Test
    void appUserTest() {
        RestaurationStagiaire restaurationStagiaire = getRestaurationStagiaireRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        restaurationStagiaire.setAppUser(appUserBack);
        assertThat(restaurationStagiaire.getAppUser()).isEqualTo(appUserBack);

        restaurationStagiaire.appUser(null);
        assertThat(restaurationStagiaire.getAppUser()).isNull();
    }
}
