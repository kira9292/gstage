package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AssistantGWTE;
import sn.sonatel.dsi.ins.imoc.service.dto.AssistantGWTEDTO;

/**
 * Mapper for the entity {@link AssistantGWTE} and its DTO {@link AssistantGWTEDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssistantGWTEMapper extends EntityMapper<AssistantGWTEDTO, AssistantGWTE> {}
