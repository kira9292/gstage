package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.JwtTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class JwtTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Jwt.class);
        Jwt jwt1 = getJwtSample1();
        Jwt jwt2 = new Jwt();
        assertThat(jwt1).isNotEqualTo(jwt2);

        jwt2.setId(jwt1.getId());
        assertThat(jwt1).isEqualTo(jwt2);

        jwt2 = getJwtSample2();
        assertThat(jwt1).isNotEqualTo(jwt2);
    }

    @Test
    void appUserTest() {
        Jwt jwt = getJwtRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        jwt.setAppUser(appUserBack);
        assertThat(jwt.getAppUser()).isEqualTo(appUserBack);

        jwt.appUser(null);
        assertThat(jwt.getAppUser()).isNull();
    }
}
