package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.dto.PersonDTO;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {
    @Mapping(target = "sponsor", source = "sponsor", qualifiedByName = "institutionName")
    @Mapping(target = "person", source = "person", qualifiedByName = "personName")
    @Mapping(target = "product", source = "product", qualifiedByName = "workName")
    RoleDTO toDto(Role s);

    @Named("institutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstitutionDTO toDtoInstitutionName(Institution institution);

    @Named("personName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PersonDTO toDtoPersonName(Person person);

    @Named("workName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WorkDTO toDtoWorkName(Work work);
}
