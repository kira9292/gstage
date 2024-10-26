package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.DfcAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.DfcTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DfcMapperTest {

    private DfcMapper dfcMapper;

    @BeforeEach
    void setUp() {
        dfcMapper = new DfcMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDfcSample1();
        var actual = dfcMapper.toEntity(dfcMapper.toDto(expected));
        assertDfcAllPropertiesEquals(expected, actual);
    }
}
