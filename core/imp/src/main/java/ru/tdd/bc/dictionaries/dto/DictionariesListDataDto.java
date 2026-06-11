package ru.tdd.bc.dictionaries.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.tdd.bc.dto.ListData;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 */
@Schema(description = "Dto со списком справочников")
public class DictionariesListDataDto extends ListData<DictionaryDto> {

    public DictionariesListDataDto() {}

    public DictionariesListDataDto(
            List<DictionaryDto> data,
            int totalPages,
            int totalCount,
            int count,
            int page
    ) {
        super(data, totalPages, totalCount, count, page);
    }
}
