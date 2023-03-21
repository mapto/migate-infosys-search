package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Location;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.dto.LocationDTO;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Institution} and its DTO {@link InstitutionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstitutionMapper extends EntityMapper<InstitutionDTO, Institution> {
    @Mapping(target = "institutionRoleNames", source = "institutionRoleNames", qualifiedByName = "roleNameSet")
    @Mapping(target = "site", source = "site", qualifiedByName = "locationAddress")
    InstitutionDTO toDto(Institution s);

    @Mapping(target = "removeInstitutionRoleName", ignore = true)
    Institution toEntity(InstitutionDTO institutionDTO);

    @Named("roleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleDTO toDtoRoleName(Role role);

    @Named("roleNameSet")
    default Set<RoleDTO> toDtoRoleNameSet(Set<Role> role) {
        return role.stream().map(this::toDtoRoleName).collect(Collectors.toSet());
    }

    @Named("locationAddress")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "address", source = "address")
    LocationDTO toDtoLocationAddress(Location location);
}
