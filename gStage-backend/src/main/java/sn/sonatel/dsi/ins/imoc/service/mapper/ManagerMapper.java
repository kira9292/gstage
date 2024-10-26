package sn.sonatel.dsi.ins.imoc.service.mapper;

import org.mapstruct.*;
import sn.sonatel.dsi.ins.imoc.domain.AppService;
import sn.sonatel.dsi.ins.imoc.domain.Manager;
import sn.sonatel.dsi.ins.imoc.service.dto.AppServiceDTO;
import sn.sonatel.dsi.ins.imoc.service.dto.ManagerDTO;

/**
 * Mapper for the entity {@link Manager} and its DTO {@link ManagerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ManagerMapper extends EntityMapper<ManagerDTO, Manager> {
    @Mapping(target = "service", source = "service", qualifiedByName = "appServiceId")
    ManagerDTO toDto(Manager s);

    @Named("appServiceId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AppServiceDTO toDtoAppServiceId(AppService appService);
}
