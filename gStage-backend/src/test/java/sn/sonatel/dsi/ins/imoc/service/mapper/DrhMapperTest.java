package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.DrhAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.DrhTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrhMapperTest {

    private DrhMapper drhMapper;

    @BeforeEach
    void setUp() {
        drhMapper = new DrhMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDrhSample1();
        var actual = drhMapper.toEntity(drhMapper.toDto(expected));
        assertDrhAllPropertiesEquals(expected, actual);
    }
}
