package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Work;
import it.unimi.dllcm.migate.domain.WorkGroup;
import it.unimi.dllcm.migate.service.dto.WorkDTO;
import it.unimi.dllcm.migate.service.dto.WorkGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Work} and its DTO {@link WorkDTO}.
 */
@Mapper(componentModel = "spring")
public interface WorkMapper extends EntityMapper<WorkDTO, Work> {
    @Mapping(target = "collection", source = "collection", qualifiedByName = "workGroupName")
    WorkDTO toDto(Work s);

    @Named("workGroupName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WorkGroupDTO toDtoWorkGroupName(WorkGroup workGroup);
}
