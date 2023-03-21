package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Work} and its DTO {@link WorkDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkMapper extends EntityMapper<WorkDTO, Work> {
    @Mapping(target = "sponsors", source = "sponsors", qualifiedByName = "institutionNameSet")
    @Mapping(target = "workRoleNames", source = "workRoleNames", qualifiedByName = "roleNameSet")
    WorkDTO toDto(Work s);

    @Mapping(target = "removeSponsor", ignore = true)
    @Mapping(target = "removeWorkRoleName", ignore = true)
    Work toEntity(WorkDTO workDTO);

    @Named("institutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstitutionDTO toDtoInstitutionName(Institution institution);

    @Named("institutionNameSet")
    default Set<InstitutionDTO> toDtoInstitutionNameSet(Set<Institution> institution) {
        return institution.stream().map(this::toDtoInstitutionName).collect(Collectors.toSet());
    }

    @Named("roleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleDTO toDtoRoleName(Role role);

    @Named("roleNameSet")
    default Set<RoleDTO> toDtoRoleNameSet(Set<Role> role) {
        return role.stream().map(this::toDtoRoleName).collect(Collectors.toSet());
    }
}
