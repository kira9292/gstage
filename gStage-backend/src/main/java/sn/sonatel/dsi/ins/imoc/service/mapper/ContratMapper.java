package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.Drh;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationFinStageDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.DrhDTO;

/**
 * Mapper for the entity {@link Contrat} and its DTO {@link ContratDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContratMapper extends EntityMapper<ContratDTO, Contrat> {
    @Mapping(target = "attestationFinStage", source = "attestationFinStage", qualifiedByName = "attestationFinStageId")
    @Mapping(target = "drh", source = "drh", qualifiedByName = "drhId")
    @Mapping(target = "assistantGWTECreator", source = "assistantGWTECreator", qualifiedByName = "assistantGWTEId")
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatId")
    ContratDTO toDto(Contrat s);

    @Named("attestationFinStageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttestationFinStageDTO toDtoAttestationFinStageId(AttestationFinStage attestationFinStage);

    @Named("drhId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DrhDTO toDtoDrhId(Drh drh);

    @Named("assistantGWTEId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AssistantGWTEDTO toDtoAssistantGWTEId(AssistantGWTE assistantGWTE);

    @Named("candidatId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CandidatDTO toDtoCandidatId(Candidat candidat);
}
