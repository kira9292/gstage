package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.Candidat;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.service.dto.CandidatDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;

/**
 * Mapper for the entity {@link Candidat} and its DTO {@link CandidatDTO}.
 */
@Mapper(componentModel = "spring")
public interface CandidatMapper extends EntityMapper<CandidatDTO, Candidat> {
    @Mapping(target = "manager", source = "manager", qualifiedByName = "managerId")
    CandidatDTO toDto(Candidat s);

    @Named("managerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ManagerDTO toDtoManagerId(Manager manager);
}
