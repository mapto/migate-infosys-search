package it.unimi.dllcm.migate.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import it.unimi.dllcm.migate.domain.Location;
import it.unimi.dllcm.migate.repository.LocationRepository;
import it.unimi.dllcm.migate.repository.search.LocationSearchRepository;
import it.unimi.dllcm.migate.service.dto.LocationDTO;
import it.unimi.dllcm.migate.service.mapper.LocationMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Location}.
 */
@Service
@Transactional
public class LocationService {

    private final Logger log = LoggerFactory.getLogger(LocationService.class);

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    private final LocationSearchRepository locationSearchRepository;

    public LocationService(
        LocationRepository locationRepository,
        LocationMapper locationMapper,
        LocationSearchRepository locationSearchRepository
    ) {
        this.locationRepository = locationRepository;
        this.locationMapper = locationMapper;
        this.locationSearchRepository = locationSearchRepository;
    }

    /**
     * Save a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    public LocationDTO save(LocationDTO locationDTO) {
        log.debug("Request to save Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        LocationDTO result = locationMapper.toDto(location);
        locationSearchRepository.index(location);
        return result;
    }

    /**
     * Update a location.
     *
     * @param locationDTO the entity to save.
     * @return the persisted entity.
     */
    public LocationDTO update(LocationDTO locationDTO) {
        log.debug("Request to update Location : {}", locationDTO);
        Location location = locationMapper.toEntity(locationDTO);
        location = locationRepository.save(location);
        LocationDTO result = locationMapper.toDto(location);
        locationSearchRepository.index(location);
        return result;
    }

    /**
     * Partially update a location.
     *
     * @param locationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LocationDTO> partialUpdate(LocationDTO locationDTO) {
        log.debug("Request to partially update Location : {}", locationDTO);

        return locationRepository
            .findById(locationDTO.getId())
            .map(existingLocation -> {
                locationMapper.partialUpdate(existingLocation, locationDTO);

                return existingLocation;
            })
            .map(locationRepository::save)
            .map(savedLocation -> {
                locationSearchRepository.save(savedLocation);

                return savedLocation;
            })
            .map(locationMapper::toDto);
    }

    /**
     * Get all the locations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LocationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Locations");
        return locationRepository.findAll(pageable).map(locationMapper::toDto);
    }

    /**
     * Get one location by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LocationDTO> findOne(Long id) {
        log.debug("Request to get Location : {}", id);
        return locationRepository.findById(id).map(locationMapper::toDto);
    }

    /**
     * Delete the location by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Location : {}", id);
        locationRepository.deleteById(id);
        locationSearchRepository.deleteById(id);
    }

    /**
     * Search for the location corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LocationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Locations for query {}", query);
        return locationSearchRepository.search(query, pageable).map(locationMapper::toDto);
    }
}
