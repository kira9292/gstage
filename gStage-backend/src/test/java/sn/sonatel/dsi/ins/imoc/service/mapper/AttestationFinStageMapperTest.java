package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.AttestationFinStageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttestationFinStageMapperTest {

    private AttestationFinStageMapper attestationFinStageMapper;

    @BeforeEach
    void setUp() {
        attestationFinStageMapper = new AttestationFinStageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAttestationFinStageSample1();
        var actual = attestationFinStageMapper.toEntity(attestationFinStageMapper.toDto(expected));
        assertAttestationFinStageAllPropertiesEquals(expected, actual);
    }
}
