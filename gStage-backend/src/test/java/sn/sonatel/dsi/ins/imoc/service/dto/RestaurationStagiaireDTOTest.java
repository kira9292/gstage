package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class RestaurationStagiaireDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurationStagiaireDTO.class);
        RestaurationStagiaireDTO restaurationStagiaireDTO1 = new RestaurationStagiaireDTO();
        restaurationStagiaireDTO1.setId(1L);
        RestaurationStagiaireDTO restaurationStagiaireDTO2 = new RestaurationStagiaireDTO();
        assertThat(restaurationStagiaireDTO1).isNotEqualTo(restaurationStagiaireDTO2);
        restaurationStagiaireDTO2.setId(restaurationStagiaireDTO1.getId());
        assertThat(restaurationStagiaireDTO1).isEqualTo(restaurationStagiaireDTO2);
        restaurationStagiaireDTO2.setId(2L);
        assertThat(restaurationStagiaireDTO1).isNotEqualTo(restaurationStagiaireDTO2);
        restaurationStagiaireDTO1.setId(null);
        assertThat(restaurationStagiaireDTO1).isNotEqualTo(restaurationStagiaireDTO2);
    }
}
