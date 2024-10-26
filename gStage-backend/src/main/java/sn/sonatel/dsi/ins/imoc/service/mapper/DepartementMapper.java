package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.service.dto.DepartementDTO;

/**
 * Mapper for the entity {@link Departement} and its DTO {@link DepartementDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepartementMapper extends EntityMapper<DepartementDTO, Departement> {}
