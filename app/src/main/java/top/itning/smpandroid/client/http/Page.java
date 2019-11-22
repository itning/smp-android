package top.itning.smpandroid.client.http;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author itning
 */
@Data
public class Page<T> implements Serializable {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private boolean last;
    private int number;
    private int size;
    private int numberOfElements;
    private boolean first;
    private boolean empty;
    private Sort sort;
    private Pageable pageable;

    @Data
    public static class Sort implements Serializable {
        private boolean sorted;
        private boolean unsorted;
        private boolean empty;
    }

    @Data
    public static class Pageable implements Serializable {
        private Sort sort;
        private int offset;
        private int pageNumber;
        private int pageSize;
        private boolean unpaged;
        private boolean paged;
    }
}
