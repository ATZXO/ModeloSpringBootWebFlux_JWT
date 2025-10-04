package com.atz.webflux.pagination;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageSupport<T> {

    private static final String FIRST_PAGE_NUMBER = "0";
    private static final String DEFAULT_PAGE_SIZE = "10";

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;

    @JsonProperty
    public long totalPages() {
        return pageSize > 0 ? (totalElements - 1) / pageSize + 1 : 0;
    }

    @JsonProperty
    public boolean isFirst() {
        return pageNumber == Integer.parseInt(FIRST_PAGE_NUMBER);
    }

    @JsonProperty
    public boolean isLast() {
        return (long) (pageNumber + 1) * pageSize >= totalElements;
    }
}
