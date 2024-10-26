package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DrhTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DrhTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Drh.class);
        Drh drh1 = getDrhSample1();
        Drh drh2 = new Drh();
        assertThat(drh1).isNotEqualTo(drh2);

        drh2.setId(drh1.getId());
        assertThat(drh1).isEqualTo(drh2);

        drh2 = getDrhSample2();
        assertThat(drh1).isNotEqualTo(drh2);
    }

    @Test
    void contratsTest() {
        Drh drh = getDrhRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        drh.addContrats(contratBack);
        assertThat(drh.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getDrh()).isEqualTo(drh);

        drh.removeContrats(contratBack);
        assertThat(drh.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getDrh()).isNull();

        drh.contrats(new HashSet<>(Set.of(contratBack)));
        assertThat(drh.getContrats()).containsOnly(contratBack);
        assertThat(contratBack.getDrh()).isEqualTo(drh);

        drh.setContrats(new HashSet<>());
        assertThat(drh.getContrats()).doesNotContain(contratBack);
        assertThat(contratBack.getDrh()).isNull();
    }
}
