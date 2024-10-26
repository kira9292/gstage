package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AssistantGWTEDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssistantGWTEDTO.class);
        AssistantGWTEDTO assistantGWTEDTO1 = new AssistantGWTEDTO();
        assistantGWTEDTO1.setId(1L);
        AssistantGWTEDTO assistantGWTEDTO2 = new AssistantGWTEDTO();
        assertThat(assistantGWTEDTO1).isNotEqualTo(assistantGWTEDTO2);
        assistantGWTEDTO2.setId(assistantGWTEDTO1.getId());
        assertThat(assistantGWTEDTO1).isEqualTo(assistantGWTEDTO2);
        assistantGWTEDTO2.setId(2L);
        assertThat(assistantGWTEDTO1).isNotEqualTo(assistantGWTEDTO2);
        assistantGWTEDTO1.setId(null);
        assertThat(assistantGWTEDTO1).isNotEqualTo(assistantGWTEDTO2);
    }
}
