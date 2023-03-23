package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Institution} and its DTO {@link InstitutionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstitutionMapper extends EntityMapper<InstitutionDTO, Institution> {
    @Mapping(target = "position", source = "position", qualifiedByName = "roleName")
    InstitutionDTO toDto(Institution s);

    @Named("roleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    RoleDTO toDtoRoleName(Role role);
}
