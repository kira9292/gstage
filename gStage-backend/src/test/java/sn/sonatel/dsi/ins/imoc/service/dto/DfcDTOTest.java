package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DfcDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DfcDTO.class);
        DfcDTO dfcDTO1 = new DfcDTO();
        dfcDTO1.setId(1L);
        DfcDTO dfcDTO2 = new DfcDTO();
        assertThat(dfcDTO1).isNotEqualTo(dfcDTO2);
        dfcDTO2.setId(dfcDTO1.getId());
        assertThat(dfcDTO1).isEqualTo(dfcDTO2);
        dfcDTO2.setId(2L);
        assertThat(dfcDTO1).isNotEqualTo(dfcDTO2);
        dfcDTO1.setId(null);
        assertThat(dfcDTO1).isNotEqualTo(dfcDTO2);
    }
}
