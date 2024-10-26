package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class ValidationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ValidationDTO.class);
        ValidationDTO validationDTO1 = new ValidationDTO();
        validationDTO1.setId(1L);
        ValidationDTO validationDTO2 = new ValidationDTO();
        assertThat(validationDTO1).isNotEqualTo(validationDTO2);
        validationDTO2.setId(validationDTO1.getId());
        assertThat(validationDTO1).isEqualTo(validationDTO2);
        validationDTO2.setId(2L);
        assertThat(validationDTO1).isNotEqualTo(validationDTO2);
        validationDTO1.setId(null);
        assertThat(validationDTO1).isNotEqualTo(validationDTO2);
    }
}
