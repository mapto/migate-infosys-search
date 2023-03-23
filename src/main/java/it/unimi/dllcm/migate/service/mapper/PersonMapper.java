package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.service.dto.PersonDTO;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {
    @Mapping(target = "job", source = "job", qualifiedByName = "roleName")
    PersonDTO toDto(Person s);

    @Named("roleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleDTO toDtoRoleName(Role role);
}
