package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class BusinessUnitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BusinessUnit.class);
        BusinessUnit businessUnit1 = getBusinessUnitSample1();
        BusinessUnit businessUnit2 = new BusinessUnit();
        assertThat(businessUnit1).isNotEqualTo(businessUnit2);

        businessUnit2.setId(businessUnit1.getId());
        assertThat(businessUnit1).isEqualTo(businessUnit2);

        businessUnit2 = getBusinessUnitSample2();
        assertThat(businessUnit1).isNotEqualTo(businessUnit2);
    }

    @Test
    void demandeStagesTest() {
        BusinessUnit businessUnit = getBusinessUnitRandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        businessUnit.addDemandeStages(demandeStageBack);
        assertThat(businessUnit.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getBusinessUnit()).isEqualTo(businessUnit);

        businessUnit.removeDemandeStages(demandeStageBack);
        assertThat(businessUnit.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getBusinessUnit()).isNull();

        businessUnit.demandeStages(new HashSet<>(Set.of(demandeStageBack)));
        assertThat(businessUnit.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getBusinessUnit()).isEqualTo(businessUnit);

        businessUnit.setDemandeStages(new HashSet<>());
        assertThat(businessUnit.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getBusinessUnit()).isNull();
    }
}
