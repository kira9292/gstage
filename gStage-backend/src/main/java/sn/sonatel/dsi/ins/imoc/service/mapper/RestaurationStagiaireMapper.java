package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.RestaurationStagiaire;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.RestaurationStagiaireDTO;

/**
 * Mapper for the entity {@link RestaurationStagiaire} and its DTO {@link RestaurationStagiaireDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurationStagiaireMapper extends EntityMapper<RestaurationStagiaireDTO, RestaurationStagiaire> {
    @Mapping(target = "candidat", source = "candidat", qualifiedByName = "candidatId")
    RestaurationStagiaireDTO toDto(RestaurationStagiaire s);

    @Named("candidatId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CandidatDTO toDtoCandidatId(Candidat candidat);
}
