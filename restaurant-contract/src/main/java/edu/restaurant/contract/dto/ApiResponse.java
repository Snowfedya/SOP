package edu.restaurant.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private T data;

    private List<Link> links;

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(T data, List<Link> links) {
        this.data = data;
        this.links = links;
    }

    // Getters and Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
