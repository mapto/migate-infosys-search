package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Institution;
import it.unimi.dllcm.migate.service.dto.InstitutionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Institution} and its DTO {@link InstitutionDTO}.
 */
@Mapper(componentModel = "spring")
public interface InstitutionMapper extends EntityMapper<InstitutionDTO, Institution> {}
