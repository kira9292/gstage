package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.AppServiceAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.AppServiceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppServiceMapperTest {

    private AppServiceMapper appServiceMapper;

    @BeforeEach
    void setUp() {
        appServiceMapper = new AppServiceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAppServiceSample1();
        var actual = appServiceMapper.toEntity(appServiceMapper.toDto(expected));
        assertAppServiceAllPropertiesEquals(expected, actual);
    }
}
