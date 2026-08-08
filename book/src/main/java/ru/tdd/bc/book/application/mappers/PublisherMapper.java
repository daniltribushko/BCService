package ru.tdd.bc.book.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.tdd.bc.book.application.dto.publisher.CreatePublisherDTO;
import ru.tdd.bc.book.application.dto.publisher.PublisherDTO;
import ru.tdd.bc.book.database.entities.Publisher;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 02.08.2026
 * Маппер издателей
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PublisherMapper {

    PublisherDTO toDto(Publisher publisher);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Publisher toEntity(CreatePublisherDTO dto);

    List<PublisherDTO> toDto(List<Publisher> publishers);
}
