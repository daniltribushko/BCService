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
    protected List<T> data;

    @Schema(
            name = "page",
            description = "Номер страницы с данными",
            type = "integer",
            format = "int32",
            example = "0"
    )
    protected int page;

    @Schema(
            name = "count",
            description = "Количество данных на одной странице",
            type = "integer",
            format = "int32",
            example = "10"
    )
    protected int count;

    @Schema(
            name = "totalPages",
            description = "Общее количество страниц с данными",
            type = "integer",
            format = "int32",
            example = "2"
    )
    protected int totalPages;

    @Schema(
            name = "totalCount",
            description = "Общее количество данных",
            type = "integer",
            format = "int64",
            example = "1000"
    )
    protected long totalCount;

    public ListData() {}

    public ListData(List<T> data, int totalPages, long totalCount, int count, int page) {
        this.data = data;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.count = count;
        this.page = page;
    }

    public static abstract class Builder<L extends ListData<T>, T> {
        private List<T> data;

        private int page;

        private int count;

        private int totalPages;

        private long totalCount;

        public Builder<L, T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public Builder<L, T> page(int page) {
            this.page = page;
            return this;
        }

        public Builder<L, T> count(int count) {
            this.count = count;
            return this;
        }

        public Builder<L, T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder<L, T> totalCount(long totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        protected L build(L listData) {
            listData.data = this.data;
            listData.page = this.page;
            listData.count = this.count;
            listData.totalCount = this.totalCount;
            listData.totalPages = this.totalPages;

            return listData;
        }

        public abstract L build();
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

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
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
