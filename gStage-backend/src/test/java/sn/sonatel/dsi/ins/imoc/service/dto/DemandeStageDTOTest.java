package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DemandeStageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DemandeStageDTO.class);
        DemandeStageDTO demandeStageDTO1 = new DemandeStageDTO();
        demandeStageDTO1.setId(1L);
        DemandeStageDTO demandeStageDTO2 = new DemandeStageDTO();
        assertThat(demandeStageDTO1).isNotEqualTo(demandeStageDTO2);
        demandeStageDTO2.setId(demandeStageDTO1.getId());
        assertThat(demandeStageDTO1).isEqualTo(demandeStageDTO2);
        demandeStageDTO2.setId(2L);
        assertThat(demandeStageDTO1).isNotEqualTo(demandeStageDTO2);
        demandeStageDTO1.setId(null);
        assertThat(demandeStageDTO1).isNotEqualTo(demandeStageDTO2);
    }
}
