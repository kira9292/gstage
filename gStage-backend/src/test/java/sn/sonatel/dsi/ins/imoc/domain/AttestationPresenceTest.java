package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ContratTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ManagerTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AttestationPresenceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AttestationPresence.class);
        AttestationPresence attestationPresence1 = getAttestationPresenceSample1();
        AttestationPresence attestationPresence2 = new AttestationPresence();
        assertThat(attestationPresence1).isNotEqualTo(attestationPresence2);

        attestationPresence2.setId(attestationPresence1.getId());
        assertThat(attestationPresence1).isEqualTo(attestationPresence2);

        attestationPresence2 = getAttestationPresenceSample2();
        assertThat(attestationPresence1).isNotEqualTo(attestationPresence2);
    }

    @Test
    void validationsTest() {
        AttestationPresence attestationPresence = getAttestationPresenceRandomSampleGenerator();
        Validation validationBack = getValidationRandomSampleGenerator();

        attestationPresence.addValidations(validationBack);
        assertThat(attestationPresence.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getAttestationPresence()).isEqualTo(attestationPresence);

        attestationPresence.removeValidations(validationBack);
        assertThat(attestationPresence.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getAttestationPresence()).isNull();

        attestationPresence.validations(new HashSet<>(Set.of(validationBack)));
        assertThat(attestationPresence.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getAttestationPresence()).isEqualTo(attestationPresence);

        attestationPresence.setValidations(new HashSet<>());
        assertThat(attestationPresence.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getAttestationPresence()).isNull();
    }

    @Test
    void contratTest() {
        AttestationPresence attestationPresence = getAttestationPresenceRandomSampleGenerator();
        Contrat contratBack = getContratRandomSampleGenerator();

        attestationPresence.setContrat(contratBack);
        assertThat(attestationPresence.getContrat()).isEqualTo(contratBack);

        attestationPresence.contrat(null);
        assertThat(attestationPresence.getContrat()).isNull();
    }

    @Test
    void managerTest() {
        AttestationPresence attestationPresence = getAttestationPresenceRandomSampleGenerator();
        Manager managerBack = getManagerRandomSampleGenerator();

        attestationPresence.setManager(managerBack);
        assertThat(attestationPresence.getManager()).isEqualTo(managerBack);

        attestationPresence.manager(null);
        assertThat(attestationPresence.getManager()).isNull();
    }

    @Test
    void etatPaiementTest() {
        AttestationPresence attestationPresence = getAttestationPresenceRandomSampleGenerator();
        EtatPaiement etatPaiementBack = getEtatPaiementRandomSampleGenerator();

        attestationPresence.setEtatPaiement(etatPaiementBack);
        assertThat(attestationPresence.getEtatPaiement()).isEqualTo(etatPaiementBack);

        attestationPresence.etatPaiement(null);
        assertThat(attestationPresence.getEtatPaiement()).isNull();
    }
}
