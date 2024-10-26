package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.DepartementAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.DepartementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DepartementMapperTest {

    private DepartementMapper departementMapper;

    @BeforeEach
    void setUp() {
        departementMapper = new DepartementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDepartementSample1();
        var actual = departementMapper.toEntity(departementMapper.toDto(expected));
        assertDepartementAllPropertiesEquals(expected, actual);
    }
}
