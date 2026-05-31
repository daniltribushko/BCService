package ru.tdd.book.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.tdd.book.controllers.dto.GenreDto;
import ru.tdd.book.controllers.dto.UpdateGenreDto;
import ru.tdd.book.database.entities.Genre;

/**
 * @author Tribushko Danil
 * @since 02.06.2026
 * Маппер жанов
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GenreMapper {

    @Mapping(target = "id", ignore = true)
    void update(UpdateGenreDto dto, @MappingTarget Genre genre);

    GenreDto toDto(Genre entity);
}
