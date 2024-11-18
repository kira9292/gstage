package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.StagiairesProposerTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class StagiairesProposerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StagiairesProposer.class);
        StagiairesProposer stagiairesProposer1 = getStagiairesProposerSample1();
        StagiairesProposer stagiairesProposer2 = new StagiairesProposer();
        assertThat(stagiairesProposer1).isNotEqualTo(stagiairesProposer2);

        stagiairesProposer2.setId(stagiairesProposer1.getId());
        assertThat(stagiairesProposer1).isEqualTo(stagiairesProposer2);

        stagiairesProposer2 = getStagiairesProposerSample2();
        assertThat(stagiairesProposer1).isNotEqualTo(stagiairesProposer2);
    }
}
