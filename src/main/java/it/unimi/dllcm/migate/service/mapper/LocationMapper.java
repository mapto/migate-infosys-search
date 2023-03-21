package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Location;
import it.unimi.dllcm.migate.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Location} and its DTO {@link LocationDTO}.
 */
@Mapper(componentModel = "spring")
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {}
