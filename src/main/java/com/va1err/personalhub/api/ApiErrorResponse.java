package com.va1err.personalhub.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record ApiErrorResponse(
    int status,
    String message,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ApiFieldError> errors
) {
}
