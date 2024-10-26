package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.AssistantGWTEAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.AssistantGWTETestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssistantGWTEMapperTest {

    private AssistantGWTEMapper assistantGWTEMapper;

    @BeforeEach
    void setUp() {
        assistantGWTEMapper = new AssistantGWTEMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssistantGWTESample1();
        var actual = assistantGWTEMapper.toEntity(assistantGWTEMapper.toDto(expected));
        assertAssistantGWTEAllPropertiesEquals(expected, actual);
    }
}
