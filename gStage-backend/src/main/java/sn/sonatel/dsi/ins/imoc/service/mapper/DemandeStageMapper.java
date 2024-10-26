package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.DemandeStage;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.BusinessUnitDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.DemandeStageDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.DepartementDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;

/**
 * Mapper for the entity {@link DemandeStage} and its DTO {@link DemandeStageDTO}.
 */
@Mapper(componentModel = "spring")
public interface DemandeStageMapper extends EntityMapper<DemandeStageDTO, DemandeStage> {
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatId")
    @Mapping(target = "assistantGWTE", source = "assistantGWTE", qualifiedByName = "assistantGWTEId")
    @Mapping(target = "manager", source = "manager", qualifiedByName = "managerId")
    @Mapping(target = "departement", source = "departement", qualifiedByName = "departementId")
    @Mapping(target = "businessUnit", source = "businessUnit", qualifiedByName = "businessUnitId")
    DemandeStageDTO toDto(DemandeStage s);

    @Named("candidatId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CandidatDTO toDtoCandidatId(Candidat candidat);

    @Named("assistantGWTEId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssistantGWTEDTO toDtoAssistantGWTEId(AssistantGWTE assistantGWTE);

    @Named("managerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ManagerDTO toDtoManagerId(Manager manager);

    @Named("departementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DepartementDTO toDtoDepartementId(Departement departement);

    @Named("businessUnitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BusinessUnitDTO toDtoBusinessUnitId(BusinessUnit businessUnit);
}
