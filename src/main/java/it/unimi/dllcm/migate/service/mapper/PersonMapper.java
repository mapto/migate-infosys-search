package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.service.dto.PersonDTO;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {
    @Mapping(target = "works", source = "works", qualifiedByName = "workNameSet")
    @Mapping(target = "responsibilities", source = "responsibilities", qualifiedByName = "roleNameSet")
    PersonDTO toDto(Person s);

    @Mapping(target = "removeWork", ignore = true)
    @Mapping(target = "removeResponsibility", ignore = true)
    Person toEntity(PersonDTO personDTO);

    @Named("workName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WorkDTO toDtoWorkName(Work work);

    @Named("workNameSet")
    default Set<WorkDTO> toDtoWorkNameSet(Set<Work> work) {
        return work.stream().map(this::toDtoWorkName).collect(Collectors.toSet());
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
