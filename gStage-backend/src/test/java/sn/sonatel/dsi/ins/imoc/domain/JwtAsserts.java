package sn.sonatel.dsi.ins.imoc.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJwtAllPropertiesEquals(Jwt expected, Jwt actual) {
        assertJwtAutoGeneratedPropertiesEquals(expected, actual);
        assertJwtAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJwtAllUpdatablePropertiesEquals(Jwt expected, Jwt actual) {
        assertJwtUpdatableFieldsEquals(expected, actual);
        assertJwtUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJwtAutoGeneratedPropertiesEquals(Jwt expected, Jwt actual) {
        assertThat(expected)
            .as("Verify Jwt auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJwtUpdatableFieldsEquals(Jwt expected, Jwt actual) {
        assertThat(expected)
            .as("Verify Jwt relevant properties")
            .satisfies(e -> assertThat(e.getDesactive()).as("check desactive").isEqualTo(actual.getDesactive()))
            .satisfies(e -> assertThat(e.getExpire()).as("check expire").isEqualTo(actual.getExpire()))
            .satisfies(e -> assertThat(e.getValeur()).as("check valeur").isEqualTo(actual.getValeur()))
            .satisfies(e -> assertThat(e.getValeurContentType()).as("check valeur contenty type").isEqualTo(actual.getValeurContentType()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertJwtUpdatableRelationshipsEquals(Jwt expected, Jwt actual) {
        assertThat(expected)
            .as("Verify Jwt relationships")
            .satisfies(e -> assertThat(e.getAppUser()).as("check appUser").isEqualTo(actual.getAppUser()));
    }
}
