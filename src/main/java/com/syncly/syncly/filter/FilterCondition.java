package com.syncly.syncly.filter;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterCondition {
    private String field;
    private FilterOperator operator;
    private Object[] values;
}