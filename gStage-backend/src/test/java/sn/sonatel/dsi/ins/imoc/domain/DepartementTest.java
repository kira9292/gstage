package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DepartementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ServiceTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class DepartementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Departement.class);
        Departement departement1 = getDepartementSample1();
        Departement departement2 = new Departement();
        assertThat(departement1).isNotEqualTo(departement2);

        departement2.setId(departement1.getId());
        assertThat(departement1).isEqualTo(departement2);

        departement2 = getDepartementSample2();
        assertThat(departement1).isNotEqualTo(departement2);
    }

    @Test
    void servicesTest() {
        Departement departement = getDepartementRandomSampleGenerator();
        Service serviceBack = getServiceRandomSampleGenerator();

        departement.addServices(serviceBack);
        assertThat(departement.getServices()).containsOnly(serviceBack);
        assertThat(serviceBack.getDepartemen()).isEqualTo(departement);

        departement.removeServices(serviceBack);
        assertThat(departement.getServices()).doesNotContain(serviceBack);
        assertThat(serviceBack.getDepartemen()).isNull();

        departement.services(new HashSet<>(Set.of(serviceBack)));
        assertThat(departement.getServices()).containsOnly(serviceBack);
        assertThat(serviceBack.getDepartemen()).isEqualTo(departement);

        departement.setServices(new HashSet<>());
        assertThat(departement.getServices()).doesNotContain(serviceBack);
        assertThat(serviceBack.getDepartemen()).isNull();
    }

    @Test
    void demandeStagesTest() {
        Departement departement = getDepartementRandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        departement.addDemandeStages(demandeStageBack);
        assertThat(departement.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getDepartement()).isEqualTo(departement);

        departement.removeDemandeStages(demandeStageBack);
        assertThat(departement.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getDepartement()).isNull();

        departement.demandeStages(new HashSet<>(Set.of(demandeStageBack)));
        assertThat(departement.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getDepartement()).isEqualTo(departement);

        departement.setDemandeStages(new HashSet<>());
        assertThat(departement.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getDepartement()).isNull();
    }
}
