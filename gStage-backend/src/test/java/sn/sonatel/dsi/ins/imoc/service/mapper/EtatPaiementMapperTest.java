package sn.sonatel.dsi.ins.imoc.service.mapper;

import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementAsserts.*;
import static sn.sonatel.dsi.ins.imoc.domain.EtatPaiementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EtatPaiementMapperTest {

    private EtatPaiementMapper etatPaiementMapper;

    @BeforeEach
    void setUp() {
        etatPaiementMapper = new EtatPaiementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEtatPaiementSample1();
        var actual = etatPaiementMapper.toEntity(etatPaiementMapper.toDto(expected));
        assertEtatPaiementAllPropertiesEquals(expected, actual);
    }
}
