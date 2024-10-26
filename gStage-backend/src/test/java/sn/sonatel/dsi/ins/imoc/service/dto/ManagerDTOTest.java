package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ManagerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ManagerDTO.class);
        ManagerDTO managerDTO1 = new ManagerDTO();
        managerDTO1.setId(1L);
        ManagerDTO managerDTO2 = new ManagerDTO();
        assertThat(managerDTO1).isNotEqualTo(managerDTO2);
        managerDTO2.setId(managerDTO1.getId());
        assertThat(managerDTO1).isEqualTo(managerDTO2);
        managerDTO2.setId(2L);
        assertThat(managerDTO1).isNotEqualTo(managerDTO2);
        managerDTO1.setId(null);
        assertThat(managerDTO1).isNotEqualTo(managerDTO2);
    }
}
