package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.service.dto.BusinessUnitDTO;

/**
 * Mapper for the entity {@link BusinessUnit} and its DTO {@link BusinessUnitDTO}.
 */
@Mapper(componentModel = "spring")
public interface BusinessUnitMapper extends EntityMapper<BusinessUnitDTO, BusinessUnit> {}
