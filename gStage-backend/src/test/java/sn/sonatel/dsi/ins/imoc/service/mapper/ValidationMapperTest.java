package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.ValidationAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.ValidationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidationMapperTest {

    private ValidationMapper validationMapper;

    @BeforeEach
    void setUp() {
        validationMapper = new ValidationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getValidationSample1();
        var actual = validationMapper.toEntity(validationMapper.toDto(expected));
        assertValidationAllPropertiesEquals(expected, actual);
    }
}
