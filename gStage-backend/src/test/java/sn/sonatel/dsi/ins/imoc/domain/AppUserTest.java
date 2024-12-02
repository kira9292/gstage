package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.sonatel.dsi.ins.imoc.domain.AppUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.CandidatTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.JwtTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.NotificationTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaireTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.RoleTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ServiceTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationStatusUserTestSamples.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import sn.sonatel.dsi.ins.imoc.web.rest.TestUtil;

class AppUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppUser.class);
        AppUser appUser1 = getAppUserSample1();
        AppUser appUser2 = new AppUser();
        assertThat(appUser1).isNotEqualTo(appUser2);

        appUser2.setId(appUser1.getId());
        assertThat(appUser1).isEqualTo(appUser2);

        appUser2 = getAppUserSample2();
        assertThat(appUser1).isNotEqualTo(appUser2);
    }

    @Test
    void serviceTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Service serviceBack = getServiceRandomSampleGenerator();

        appUser.setService(serviceBack);
        assertThat(appUser.getService()).isEqualTo(serviceBack);

        appUser.service(null);
        assertThat(appUser.getService()).isNull();
    }

    @Test
    void demandeStageTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        DemandeStage demandeStageBack = getDemandeStageRandomSampleGenerator();

        appUser.addDemandeStage(demandeStageBack);
        assertThat(appUser.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isEqualTo(appUser);

        appUser.removeDemandeStage(demandeStageBack);
        assertThat(appUser.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isNull();

        appUser.demandeStages(new HashSet<>(Set.of(demandeStageBack)));
        assertThat(appUser.getDemandeStages()).containsOnly(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isEqualTo(appUser);

        appUser.setDemandeStages(new HashSet<>());
        assertThat(appUser.getDemandeStages()).doesNotContain(demandeStageBack);
        assertThat(demandeStageBack.getAppUser()).isNull();
    }

    @Test
    void candidatTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Candidat candidatBack = getCandidatRandomSampleGenerator();

        appUser.addCandidat(candidatBack);
        assertThat(appUser.getCandidats()).containsOnly(candidatBack);
        assertThat(candidatBack.getAppUser()).isEqualTo(appUser);

        appUser.removeCandidat(candidatBack);
        assertThat(appUser.getCandidats()).doesNotContain(candidatBack);
        assertThat(candidatBack.getAppUser()).isNull();

        appUser.candidats(new HashSet<>(Set.of(candidatBack)));
        assertThat(appUser.getCandidats()).containsOnly(candidatBack);
        assertThat(candidatBack.getAppUser()).isEqualTo(appUser);

        appUser.setCandidats(new HashSet<>());
        assertThat(appUser.getCandidats()).doesNotContain(candidatBack);
        assertThat(candidatBack.getAppUser()).isNull();
    }

    @Test
    void validationsTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Validation validationBack = getValidationRandomSampleGenerator();

        appUser.addValidations(validationBack);
        assertThat(appUser.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getUser()).isEqualTo(appUser);

        appUser.removeValidations(validationBack);
        assertThat(appUser.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getUser()).isNull();

        appUser.validations(new HashSet<>(Set.of(validationBack)));
        assertThat(appUser.getValidations()).containsOnly(validationBack);
        assertThat(validationBack.getUser()).isEqualTo(appUser);

        appUser.setValidations(new HashSet<>());
        assertThat(appUser.getValidations()).doesNotContain(validationBack);
        assertThat(validationBack.getUser()).isNull();
    }

    @Test
    void notificationTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        appUser.addNotification(notificationBack);
        assertThat(appUser.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getAppUser()).isEqualTo(appUser);

        appUser.removeNotification(notificationBack);
        assertThat(appUser.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getAppUser()).isNull();

        appUser.notifications(new HashSet<>(Set.of(notificationBack)));
        assertThat(appUser.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getAppUser()).isEqualTo(appUser);

        appUser.setNotifications(new HashSet<>());
        assertThat(appUser.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getAppUser()).isNull();
    }

    @Test
    void roleTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Role roleBack = getRoleRandomSampleGenerator();

        appUser.setRole(roleBack);
        assertThat(appUser.getRole()).isEqualTo(roleBack);

        appUser.role(null);
        assertThat(appUser.getRole()).isNull();
    }

    @Test
    void validationStatusUserTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        ValidationStatusUser validationStatusUserBack = getValidationStatusUserRandomSampleGenerator();

        appUser.setValidationStatusUser(validationStatusUserBack);
        assertThat(appUser.getValidationStatusUser()).isEqualTo(validationStatusUserBack);
        assertThat(validationStatusUserBack.getAppUser()).isEqualTo(appUser);

        appUser.validationStatusUser(null);
        assertThat(appUser.getValidationStatusUser()).isNull();
        assertThat(validationStatusUserBack.getAppUser()).isNull();
    }

    @Test
    void restaurationStagiaireTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        RestaurationStagiaire restaurationStagiaireBack = getRestaurationStagiaireRandomSampleGenerator();

        appUser.addRestaurationStagiaire(restaurationStagiaireBack);
        assertThat(appUser.getRestaurationStagiaires()).containsOnly(restaurationStagiaireBack);
        assertThat(restaurationStagiaireBack.getAppUser()).isEqualTo(appUser);

        appUser.removeRestaurationStagiaire(restaurationStagiaireBack);
        assertThat(appUser.getRestaurationStagiaires()).doesNotContain(restaurationStagiaireBack);
        assertThat(restaurationStagiaireBack.getAppUser()).isNull();

        appUser.restaurationStagiaires(new HashSet<>(Set.of(restaurationStagiaireBack)));
        assertThat(appUser.getRestaurationStagiaires()).containsOnly(restaurationStagiaireBack);
        assertThat(restaurationStagiaireBack.getAppUser()).isEqualTo(appUser);

        appUser.setRestaurationStagiaires(new HashSet<>());
        assertThat(appUser.getRestaurationStagiaires()).doesNotContain(restaurationStagiaireBack);
        assertThat(restaurationStagiaireBack.getAppUser()).isNull();
    }

    @Test
    void jwtTest() {
        AppUser appUser = getAppUserRandomSampleGenerator();
        Jwt jwtBack = getJwtRandomSampleGenerator();

        appUser.addJwt(jwtBack);
        assertThat(appUser.getJwts()).containsOnly(jwtBack);
        assertThat(jwtBack.getAppUser()).isEqualTo(appUser);

        appUser.removeJwt(jwtBack);
        assertThat(appUser.getJwts()).doesNotContain(jwtBack);
        assertThat(jwtBack.getAppUser()).isNull();

        appUser.jwts(new HashSet<>(Set.of(jwtBack)));
        assertThat(appUser.getJwts()).containsOnly(jwtBack);
        assertThat(jwtBack.getAppUser()).isEqualTo(appUser);

        appUser.setJwts(new HashSet<>());
        assertThat(appUser.getJwts()).doesNotContain(jwtBack);
        assertThat(jwtBack.getAppUser()).isNull();
    }
}
