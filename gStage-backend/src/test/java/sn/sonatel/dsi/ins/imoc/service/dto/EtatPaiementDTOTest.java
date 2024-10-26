package sn.sonatel.dsi.ins.imoc.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class EtatPaiementDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtatPaiementDTO.class);
        EtatPaiementDTO etatPaiementDTO1 = new EtatPaiementDTO();
        etatPaiementDTO1.setId(1L);
        EtatPaiementDTO etatPaiementDTO2 = new EtatPaiementDTO();
        assertThat(etatPaiementDTO1).isNotEqualTo(etatPaiementDTO2);
        etatPaiementDTO2.setId(etatPaiementDTO1.getId());
        assertThat(etatPaiementDTO1).isEqualTo(etatPaiementDTO2);
        etatPaiementDTO2.setId(2L);
        assertThat(etatPaiementDTO1).isNotEqualTo(etatPaiementDTO2);
        etatPaiementDTO1.setId(null);
        assertThat(etatPaiementDTO1).isNotEqualTo(etatPaiementDTO2);
    }
}
