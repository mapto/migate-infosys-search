package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Role;
import it.unimi.dllcm.migate.service.dto.RoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {}
