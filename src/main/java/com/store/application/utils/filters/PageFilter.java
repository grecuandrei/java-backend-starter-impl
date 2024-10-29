package com.store.application.utils.filters;

import lombok.Data;

import java.util.List;

@Data
public class PageFilter {
    private int page;
    private int size;
    private String sort;
    private String order;
    private List<FilterCriteria> filters;
}
