package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.Dfc;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.DfcDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.EtatPaiementDTO;

/**
 * Mapper for the entity {@link EtatPaiement} and its DTO {@link EtatPaiementDTO}.
 */
@Mapper(componentModel = "spring")
public interface EtatPaiementMapper extends EntityMapper<EtatPaiementDTO, EtatPaiement> {
    @Mapping(target = "contrat", source = "contrat", qualifiedByName = "contratId")
    @Mapping(target = "dfc", source = "dfc", qualifiedByName = "dfcId")
    @Mapping(target = "assistantGWTECreator", source = "assistantGWTECreator", qualifiedByName = "assistantGWTEId")
    EtatPaiementDTO toDto(EtatPaiement s);

    @Named("contratId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ContratDTO toDtoContratId(Contrat contrat);

    @Named("dfcId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DfcDTO toDtoDfcId(Dfc dfc);

    @Named("assistantGWTEId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssistantGWTEDTO toDtoAssistantGWTEId(AssistantGWTE assistantGWTE);
}
