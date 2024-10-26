package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DrhDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DrhDTO.class);
        DrhDTO drhDTO1 = new DrhDTO();
        drhDTO1.setId(1L);
        DrhDTO drhDTO2 = new DrhDTO();
        assertThat(drhDTO1).isNotEqualTo(drhDTO2);
        drhDTO2.setId(drhDTO1.getId());
        assertThat(drhDTO1).isEqualTo(drhDTO2);
        drhDTO2.setId(2L);
        assertThat(drhDTO1).isNotEqualTo(drhDTO2);
        drhDTO1.setId(null);
        assertThat(drhDTO1).isNotEqualTo(drhDTO2);
    }
}
