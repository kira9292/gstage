package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.Dfc;
import sn.sonatel.dsi.ins.imoc.service.dto.DfcDTO;

/**
 * Mapper for the entity {@link Dfc} and its DTO {@link DfcDTO}.
 */
@Mapper(componentModel = "spring")
public interface DfcMapper extends EntityMapper<DfcDTO, Dfc> {}
