package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.domain.Location;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import it.unimi.dllcm.migate.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "institutionName")
    LocationDTO toDto(Location s);

    @Named("institutionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    InstitutionDTO toDtoInstitutionName(Institution institution);
}
