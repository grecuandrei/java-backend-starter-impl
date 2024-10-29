package com.store.application.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomResponse<T> {
    private int page;
    private int size;
    private long total;
    private int totalPages;
    private List<T> content;
    private boolean last;
}
