package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.Drh;
import sn.sonatel.dsi.ins.imoc.service.dto.DrhDTO;

/**
 * Mapper for the entity {@link Drh} and its DTO {@link DrhDTO}.
 */
@Mapper(componentModel = "spring")
public interface DrhMapper extends EntityMapper<DrhDTO, Drh> {}
