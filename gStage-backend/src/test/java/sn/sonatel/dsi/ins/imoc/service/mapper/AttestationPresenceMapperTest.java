package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationPresenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttestationPresenceMapperTest {

    private AttestationPresenceMapper attestationPresenceMapper;

    @BeforeEach
    void setUp() {
        attestationPresenceMapper = new AttestationPresenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAttestationPresenceSample1();
        var actual = attestationPresenceMapper.toEntity(attestationPresenceMapper.toDto(expected));
        assertAttestationPresenceAllPropertiesEquals(expected, actual);
    }
}
