package sn.sonatel.dsi.ins.imoc.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sn.sonatel.dsi.ins.imoc.domain.criteria.ValidationCriteria;
import sn.sonatel.dsi.ins.imoc.repository.ValidationRepository;
import sn.sonatel.dsi.ins.imoc.service.dto.ValidationDTO;
import sn.sonatel.dsi.ins.imoc.service.mapper.ValidationMapper;

/**
 * Service Implementation for managing {@link sn.sonatel.dsi.ins.imoc.domain.Validation}.
 */
@Service
@Transactional
public class ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationService.class);

    private final ValidationRepository validationRepository;

    private final ValidationMapper validationMapper;

    public ValidationService(ValidationRepository validationRepository, ValidationMapper validationMapper) {
        this.validationRepository = validationRepository;
        this.validationMapper = validationMapper;
    }

    /**
     * Save a validation.
     *
     * @param validationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ValidationDTO> save(ValidationDTO validationDTO) {
        LOG.debug("Request to save Validation : {}", validationDTO);
        return validationRepository.save(validationMapper.toEntity(validationDTO)).map(validationMapper::toDto);
    }

    /**
     * Update a validation.
     *
     * @param validationDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<ValidationDTO> update(ValidationDTO validationDTO) {
        LOG.debug("Request to update Validation : {}", validationDTO);
        return validationRepository.save(validationMapper.toEntity(validationDTO)).map(validationMapper::toDto);
    }

    /**
     * Partially update a validation.
     *
     * @param validationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<ValidationDTO> partialUpdate(ValidationDTO validationDTO) {
        LOG.debug("Request to partially update Validation : {}", validationDTO);

        return validationRepository
            .findById(validationDTO.getId())
            .map(existingValidation -> {
                validationMapper.partialUpdate(existingValidation, validationDTO);

                return existingValidation;
            })
            .flatMap(validationRepository::save)
            .map(validationMapper::toDto);
    }

    /**
     * Find validations by Criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<ValidationDTO> findByCriteria(ValidationCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Validations by Criteria");
        return validationRepository.findByCriteria(criteria, pageable).map(validationMapper::toDto);
    }

    /**
     * Find the count of validations by criteria.
     * @param criteria filtering criteria
     * @return the count of validations
     */
    public Mono<Long> countByCriteria(ValidationCriteria criteria) {
        LOG.debug("Request to get the count of all Validations by Criteria");
        return validationRepository.countByCriteria(criteria);
    }

    /**
     * Returns the number of validations available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return validationRepository.count();
    }

    /**
     * Get one validation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<ValidationDTO> findOne(Long id) {
        LOG.debug("Request to get Validation : {}", id);
        return validationRepository.findById(id).map(validationMapper::toDto);
    }

    /**
     * Delete the validation by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Validation : {}", id);
        return validationRepository.deleteById(id);
    }
}
