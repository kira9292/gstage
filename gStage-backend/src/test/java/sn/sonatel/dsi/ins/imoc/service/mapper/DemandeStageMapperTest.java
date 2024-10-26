package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.DemandeStageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DemandeStageMapperTest {

    private DemandeStageMapper demandeStageMapper;

    @BeforeEach
    void setUp() {
        demandeStageMapper = new DemandeStageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDemandeStageSample1();
        var actual = demandeStageMapper.toEntity(demandeStageMapper.toDto(expected));
        assertDemandeStageAllPropertiesEquals(expected, actual);
    }
}
