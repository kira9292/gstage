package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.RoleTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class RoleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role1 = getRoleSample1();
        Role role2 = new Role();
        assertThat(role1).isNotEqualTo(role2);

        role2.setId(role1.getId());
        assertThat(role1).isEqualTo(role2);

        role2 = getRoleSample2();
        assertThat(role1).isNotEqualTo(role2);
    }

    @Test
    void appUserTest() {
        Role role = getRoleRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        role.addAppUser(appUserBack);
        assertThat(role.getAppUsers()).containsOnly(appUserBack);
        assertThat(appUserBack.getRoles()).containsOnly(role);

        role.removeAppUser(appUserBack);
        assertThat(role.getAppUsers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getRoles()).doesNotContain(role);

        role.appUsers(new HashSet<>(Set.of(appUserBack)));
        assertThat(role.getAppUsers()).containsOnly(appUserBack);
        assertThat(appUserBack.getRoles()).containsOnly(role);

        role.setAppUsers(new HashSet<>());
        assertThat(role.getAppUsers()).doesNotContain(appUserBack);
        assertThat(appUserBack.getRoles()).doesNotContain(role);
    }
}
