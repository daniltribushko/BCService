package ru.tdd.bc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * @author Tribushko Danil
 * @since 11.06.2026
 * @param <T> Класс хранимых данных
 */
@Schema(description = "Dto с несколькими объектами")
public abstract class ListData<T> {

    @Schema(description = "Список объектов")
    private List<T> data;

    @Schema(
            name = "page",
            description = "Номер страницы с данными",
            type = "integer",
            format = "int32",
            example = "0"
    )
    private int page;

    @Schema(
            name = "count",
            description = "Количество данных на одной странице",
            type = "integer",
            format = "int32",
            example = "10"
    )
    private int count;

    @Schema(
            name = "totalPages",
            description = "Общее количество страниц с данными",
            type = "integer",
            format = "int32",
            example = "2"
    )
    private int totalPages;

    @Schema(
            name = "totalCount",
            description = "Общее количество данных",
            type = "integer",
            format = "int32",
            example = "1000"
    )
    private int totalCount;

    public ListData() {}

    public ListData(List<T> data, int totalPages, int totalCount, int count, int page) {
        this.data = data;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.count = count;
        this.page = page;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
