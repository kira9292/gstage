package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppServiceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DepartementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ManagerTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AppServiceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppService.class);
        AppService appService1 = getAppServiceSample1();
        AppService appService2 = new AppService();
        assertThat(appService1).isNotEqualTo(appService2);

        appService2.setId(appService1.getId());
        assertThat(appService1).isEqualTo(appService2);

        appService2 = getAppServiceSample2();
        assertThat(appService1).isNotEqualTo(appService2);
    }

    @Test
    void businessUnitTest() {
        AppService appService = getAppServiceRandomSampleGenerator();
        BusinessUnit businessUnitBack = getBusinessUnitRandomSampleGenerator();

        appService.setBusinessUnit(businessUnitBack);
        assertThat(appService.getBusinessUnit()).isEqualTo(businessUnitBack);

        appService.businessUnit(null);
        assertThat(appService.getBusinessUnit()).isNull();
    }

    @Test
    void managerTest() {
        AppService appService = getAppServiceRandomSampleGenerator();
        Manager managerBack = getManagerRandomSampleGenerator();

        appService.setManager(managerBack);
        assertThat(appService.getManager()).isEqualTo(managerBack);
        assertThat(managerBack.getService()).isEqualTo(appService);

        appService.manager(null);
        assertThat(appService.getManager()).isNull();
        assertThat(managerBack.getService()).isNull();
    }

    @Test
    void departemenTest() {
        AppService appService = getAppServiceRandomSampleGenerator();
        Departement departementBack = getDepartementRandomSampleGenerator();

        appService.setDepartemen(departementBack);
        assertThat(appService.getDepartemen()).isEqualTo(departementBack);

        appService.departemen(null);
        assertThat(appService.getDepartemen()).isNull();
    }
}
