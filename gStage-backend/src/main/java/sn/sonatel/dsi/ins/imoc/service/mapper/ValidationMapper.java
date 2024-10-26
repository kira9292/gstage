package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.domain.AttestationFinStage;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.Validation;
import sn.sonatel.dsi.ins.imoc.service.dto.AppUserDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationFinStageDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationPresenceDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ValidationDTO;

/**
 * Mapper for the entity {@link Validation} and its DTO {@link ValidationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ValidationMapper extends EntityMapper<ValidationDTO, Validation> {
    @Mapping(target = "attestationPresence", source = "attestationPresence", qualifiedByName = "attestationPresenceId")
    @Mapping(target = "contrat", source = "contrat", qualifiedByName = "contratId")
    @Mapping(target = "attestationFinStage", source = "attestationFinStage", qualifiedByName = "attestationFinStageId")
    @Mapping(target = "user", source = "user", qualifiedByName = "appUserId")
    ValidationDTO toDto(Validation s);

    @Named("attestationPresenceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttestationPresenceDTO toDtoAttestationPresenceId(AttestationPresence attestationPresence);

    @Named("contratId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ContratDTO toDtoContratId(Contrat contrat);

    @Named("attestationFinStageId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AttestationFinStageDTO toDtoAttestationFinStageId(AttestationFinStage attestationFinStage);

    @Named("appUserId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppUserDTO toDtoAppUserId(AppUser appUser);
}
