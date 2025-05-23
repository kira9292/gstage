package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationStatuscandidatAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertValidationStatuscandidatAllPropertiesEquals(
        ValidationStatuscandidat expected,
        ValidationStatuscandidat actual
    ) {
        assertValidationStatuscandidatAutoGeneratedPropertiesEquals(expected, actual);
        assertValidationStatuscandidatAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertValidationStatuscandidatAllUpdatablePropertiesEquals(
        ValidationStatuscandidat expected,
        ValidationStatuscandidat actual
    ) {
        assertValidationStatuscandidatUpdatableFieldsEquals(expected, actual);
        assertValidationStatuscandidatUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertValidationStatuscandidatAutoGeneratedPropertiesEquals(
        ValidationStatuscandidat expected,
        ValidationStatuscandidat actual
    ) {
        assertThat(expected)
            .as("Verify ValidationStatuscandidat auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertValidationStatuscandidatUpdatableFieldsEquals(
        ValidationStatuscandidat expected,
        ValidationStatuscandidat actual
    ) {
        assertThat(expected)
            .as("Verify ValidationStatuscandidat relevant properties")
            .satisfies(e -> assertThat(e.getCreation()).as("check creation").isEqualTo(actual.getCreation()))
            .satisfies(e -> assertThat(e.getExpire()).as("check expire").isEqualTo(actual.getExpire()))
            .satisfies(e -> assertThat(e.getActivation()).as("check activation").isEqualTo(actual.getActivation()))
            .satisfies(e -> assertThat(e.getCode()).as("check code").isEqualTo(actual.getCode()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertValidationStatuscandidatUpdatableRelationshipsEquals(
        ValidationStatuscandidat expected,
        ValidationStatuscandidat actual
    ) {
        assertThat(expected)
            .as("Verify ValidationStatuscandidat relationships")
            .satisfies(e -> assertThat(e.getCandidat()).as("check candidat").isEqualTo(actual.getCandidat()));
    }
}
