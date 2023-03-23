package it.unimi.dllcm.migate.service.mapper;

import it.unimi.dllcm.migate.domain.Person;
import it.unimi.dllcm.migate.service.dto.PersonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Person} and its DTO {@link PersonDTO}.
 */
@Mapper(componentModel = "spring")
public interface PersonMapper extends EntityMapper<PersonDTO, Person> {}
