package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AttestationFinStageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttestationFinStageDTO.class);
        AttestationFinStageDTO attestationFinStageDTO1 = new AttestationFinStageDTO();
        attestationFinStageDTO1.setId(1L);
        AttestationFinStageDTO attestationFinStageDTO2 = new AttestationFinStageDTO();
        assertThat(attestationFinStageDTO1).isNotEqualTo(attestationFinStageDTO2);
        attestationFinStageDTO2.setId(attestationFinStageDTO1.getId());
        assertThat(attestationFinStageDTO1).isEqualTo(attestationFinStageDTO2);
        attestationFinStageDTO2.setId(2L);
        assertThat(attestationFinStageDTO1).isNotEqualTo(attestationFinStageDTO2);
        attestationFinStageDTO1.setId(null);
        assertThat(attestationFinStageDTO1).isNotEqualTo(attestationFinStageDTO2);
    }
}
