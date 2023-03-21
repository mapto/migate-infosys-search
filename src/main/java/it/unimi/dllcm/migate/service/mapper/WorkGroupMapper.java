package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link WorkGroup} and its DTO {@link WorkGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkGroupMapper extends EntityMapper<WorkGroupDTO, WorkGroup> {
    @Mapping(target = "work", source = "work", qualifiedByName = "workName")
    WorkGroupDTO toDto(WorkGroup s);

    @Named("workName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WorkDTO toDtoWorkName(Work work);
}
