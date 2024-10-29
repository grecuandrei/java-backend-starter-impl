package com.store.application.utils.filters;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterCriteria {
    @NotBlank(message = "Filter key must not be blank")
    private String key;
    @NotNull(message = "Filter operator must not be null")
    private FilterOperator operator;
    @NotNull(message = "Filter values must not be null")
    private List<String> values;
}
