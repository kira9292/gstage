package sn.sonatel.dsi.ins.imoc.dto.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.sonatel.dsi.ins.imoc.domain.AppUser;
import sn.sonatel.dsi.ins.imoc.dto.ManagerDTO2;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    @Mapping(target = "service", source = "service.name")
    @Mapping(target = "lastName", source = "name")
    ManagerDTO2 toManagerDTO(AppUser appUser);

    @Mapping(target = "service", ignore = true)
    AppUser managerDTOToAppUser(ManagerDTO2 managerDTO);
}
