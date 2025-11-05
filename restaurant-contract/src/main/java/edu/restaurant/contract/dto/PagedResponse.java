package edu.restaurant.contract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private List<T> content;

    private PageMetadata page;

    private List<Link> links;

    public PagedResponse(List<T> content, PageMetadata page) {
        this.content = content;
        this.page = page;
    }

    // Getters and setters...
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageMetadata getPage() {
        return page;
    }

    public void setPage(PageMetadata page) {
        this.page = page;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public static class PageMetadata {
        private long size;
        private long totalElements;
        private long totalPages;
        private long number;

        public PageMetadata(long size, long totalElements, long totalPages, long number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }

        // Getters and setters...
        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public long getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(long totalPages) {
            this.totalPages = totalPages;
        }

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }
    }
}
