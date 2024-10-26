package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AttestationPresenceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttestationPresenceDTO.class);
        AttestationPresenceDTO attestationPresenceDTO1 = new AttestationPresenceDTO();
        attestationPresenceDTO1.setId(1L);
        AttestationPresenceDTO attestationPresenceDTO2 = new AttestationPresenceDTO();
        assertThat(attestationPresenceDTO1).isNotEqualTo(attestationPresenceDTO2);
        attestationPresenceDTO2.setId(attestationPresenceDTO1.getId());
        assertThat(attestationPresenceDTO1).isEqualTo(attestationPresenceDTO2);
        attestationPresenceDTO2.setId(2L);
        assertThat(attestationPresenceDTO1).isNotEqualTo(attestationPresenceDTO2);
        attestationPresenceDTO1.setId(null);
        assertThat(attestationPresenceDTO1).isNotEqualTo(attestationPresenceDTO2);
    }
}
