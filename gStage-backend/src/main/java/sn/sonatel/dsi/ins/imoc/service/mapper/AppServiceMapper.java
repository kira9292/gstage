package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AppService;
import sn.sonatel.dsi.ins.imoc.domain.BusinessUnit;
import sn.sonatel.dsi.ins.imoc.domain.Departement;
import sn.sonatel.dsi.ins.imoc.service.dto.AppServiceDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.BusinessUnitDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.DepartementDTO;

/**
 * Mapper for the entity {@link AppService} and its DTO {@link AppServiceDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppServiceMapper extends EntityMapper<AppServiceDTO, AppService> {
    @Mapping(target = "businessUnit", source = "businessUnit", qualifiedByName = "businessUnitId")
    @Mapping(target = "departemen", source = "departemen", qualifiedByName = "departementId")
    AppServiceDTO toDto(AppService s);

    @Named("businessUnitId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    BusinessUnitDTO toDtoBusinessUnitId(BusinessUnit businessUnit);

    @Named("departementId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DepartementDTO toDtoDepartementId(Departement departement);
}
