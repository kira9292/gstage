package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AttestationPresence;
import sn.sonatel.dsi.ins.imoc.domain.Contrat;
import sn.sonatel.dsi.ins.imoc.domain.EtatPaiement;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.service.dto.AttestationPresenceDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ContratDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.EtatPaiementDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;

/**
 * Mapper for the entity {@link AttestationPresence} and its DTO {@link AttestationPresenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface AttestationPresenceMapper extends EntityMapper<AttestationPresenceDTO, AttestationPresence> {
    @Mapping(target = "contrat", source = "contrat", qualifiedByName = "contratId")
    @Mapping(target = "manager", source = "manager", qualifiedByName = "managerId")
    @Mapping(target = "etatPaiement", source = "etatPaiement", qualifiedByName = "etatPaiementId")
    AttestationPresenceDTO toDto(AttestationPresence s);

    @Named("contratId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ContratDTO toDtoContratId(Contrat contrat);

    @Named("managerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ManagerDTO toDtoManagerId(Manager manager);

    @Named("etatPaiementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EtatPaiementDTO toDtoEtatPaiementId(EtatPaiement etatPaiement);
}
