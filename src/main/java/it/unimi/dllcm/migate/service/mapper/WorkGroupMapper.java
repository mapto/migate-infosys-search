package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkGroup} and its DTO {@link WorkGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkGroupMapper extends EntityMapper<WorkGroupDTO, WorkGroup> {}
