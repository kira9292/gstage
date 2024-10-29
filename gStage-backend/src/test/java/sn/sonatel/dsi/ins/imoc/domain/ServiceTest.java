package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DepartementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ServiceTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Service.class);
        Service service1 = getServiceSample1();
        Service service2 = new Service();
        assertThat(service1).isNotEqualTo(service2);

        service2.setId(service1.getId());
        assertThat(service1).isEqualTo(service2);

        service2 = getServiceSample2();
        assertThat(service1).isNotEqualTo(service2);
    }

    @Test
    void businessUnitTest() {
        Service service = getServiceRandomSampleGenerator();
        BusinessUnit businessUnitBack = getBusinessUnitRandomSampleGenerator();

        service.setBusinessUnit(businessUnitBack);
        assertThat(service.getBusinessUnit()).isEqualTo(businessUnitBack);

        service.businessUnit(null);
        assertThat(service.getBusinessUnit()).isNull();
    }

    @Test
    void appUserTest() {
        Service service = getServiceRandomSampleGenerator();
        AppUser appUserBack = getAppUserRandomSampleGenerator();

        service.setAppUser(appUserBack);
        assertThat(service.getAppUser()).isEqualTo(appUserBack);
        assertThat(appUserBack.getService()).isEqualTo(service);

        service.appUser(null);
        assertThat(service.getAppUser()).isNull();
        assertThat(appUserBack.getService()).isNull();
    }

    @Test
    void departemenTest() {
        Service service = getServiceRandomSampleGenerator();
        Departement departementBack = getDepartementRandomSampleGenerator();

        service.setDepartemen(departementBack);
        assertThat(service.getDepartemen()).isEqualTo(departementBack);

        service.departemen(null);
        assertThat(service.getDepartemen()).isNull();
    }
}
