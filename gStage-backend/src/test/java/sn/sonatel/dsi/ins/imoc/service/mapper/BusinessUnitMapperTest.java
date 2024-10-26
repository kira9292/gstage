package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.BusinessUnitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BusinessUnitMapperTest {

    private BusinessUnitMapper businessUnitMapper;

    @BeforeEach
    void setUp() {
        businessUnitMapper = new BusinessUnitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBusinessUnitSample1();
        var actual = businessUnitMapper.toEntity(businessUnitMapper.toDto(expected));
        assertBusinessUnitAllPropertiesEquals(expected, actual);
    }
}
