package edu.restaurant.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Link(
        String rel,
        String href,
        String method
) {}
