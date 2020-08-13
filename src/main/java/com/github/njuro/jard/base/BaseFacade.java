package com.github.njuro.jard.base;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Base facade providing suitable implementation of {@link BaseMapper} and delegate methods for
 * shorter code.
 */
@Component
public abstract class BaseFacade<ENTITY extends BaseEntity, DTO extends BaseDto> {

  private BaseRepository<ENTITY> repository;

  protected BaseMapper<ENTITY, DTO> mapper;

  protected DTO toDto(ENTITY entity) {
    return mapper.toDto(entity);
  }

  protected List<DTO> toDtoList(List<ENTITY> entityList) {
    return mapper.toDtoList(entityList);
  }

  protected ENTITY toEntity(DTO dto) {
    if (dto.getId() != null) {
      ENTITY existing = repository.findById(dto.getId()).orElseThrow();
      return mapper.toExistingEntity(dto, existing);
    }

    return mapper.toEntity(dto);
  }

  protected List<ENTITY> toEntityList(List<DTO> dtoList) {
    return mapper.toEntityList(dtoList);
  }

  @Autowired
  private void setMapper(BaseMapper<ENTITY, DTO> mapper) {
    this.mapper = mapper;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private void setRepository(BaseRepository<ENTITY> repository) {
    this.repository = repository;
  }
}
