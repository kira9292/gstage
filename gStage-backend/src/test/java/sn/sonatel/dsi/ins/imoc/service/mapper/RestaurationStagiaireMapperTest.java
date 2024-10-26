package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaireAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaireTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurationStagiaireMapperTest {

    private RestaurationStagiaireMapper restaurationStagiaireMapper;

    @BeforeEach
    void setUp() {
        restaurationStagiaireMapper = new RestaurationStagiaireMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRestaurationStagiaireSample1();
        var actual = restaurationStagiaireMapper.toEntity(restaurationStagiaireMapper.toDto(expected));
        assertRestaurationStagiaireAllPropertiesEquals(expected, actual);
    }
}
