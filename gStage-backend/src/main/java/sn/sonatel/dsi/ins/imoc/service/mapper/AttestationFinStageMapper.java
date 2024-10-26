package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationFinStageDTO;

/**
 * Mapper for the entity {@link AttestationFinStage} and its DTO {@link AttestationFinStageDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttestationFinStageMapper extends EntityMapper<AttestationFinStageDTO, AttestationFinStage> {}
