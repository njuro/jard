package com.github.njuro.jard.rewrite.base

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Base facade providing suitable implementation of [BaseMapper] and delegate methods for
 * shorter code.
 */
@Component
abstract class BaseFacade<ENTITY : BaseEntity, DTO : BaseDto> {

    @Autowired
    private lateinit var repository: BaseRepository<ENTITY>

    @Autowired
    protected lateinit var mapper: BaseMapper<ENTITY, DTO>

    protected fun toDto(entity: ENTITY): DTO = mapper.toDto(entity)

    protected fun toDtoList(entityList: List<ENTITY>): List<DTO> = mapper.toDtoList(entityList)

    protected fun toEntity(dto: DTO): ENTITY {
        if (dto.id != null) {
            val existing = repository.findById(dto.id).orElseThrow()
            return mapper.toExistingEntity(dto, existing)
        }
        return mapper.toEntity(dto)
    }

    protected fun toEntityList(dtoList: List<DTO>): List<ENTITY> = mapper.toEntityList(dtoList)
}
