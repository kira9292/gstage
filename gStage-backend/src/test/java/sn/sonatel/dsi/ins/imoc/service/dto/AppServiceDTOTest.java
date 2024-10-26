package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AppServiceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppServiceDTO.class);
        AppServiceDTO appServiceDTO1 = new AppServiceDTO();
        appServiceDTO1.setId(1L);
        AppServiceDTO appServiceDTO2 = new AppServiceDTO();
        assertThat(appServiceDTO1).isNotEqualTo(appServiceDTO2);
        appServiceDTO2.setId(appServiceDTO1.getId());
        assertThat(appServiceDTO1).isEqualTo(appServiceDTO2);
        appServiceDTO2.setId(2L);
        assertThat(appServiceDTO1).isNotEqualTo(appServiceDTO2);
        appServiceDTO1.setId(null);
        assertThat(appServiceDTO1).isNotEqualTo(appServiceDTO2);
    }
}
