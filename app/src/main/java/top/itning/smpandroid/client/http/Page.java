package top.itning.smpandroid.client.http;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 分页实例
 *
 * @author itning
 */
@Data
public class Page<T> implements Serializable {
    /**
     * 内容
     */
    private List<T> content;
    /**
     * 总页数
     */
    private int totalPages;
    /**
     * 总元素数
     */
    private int totalElements;
    /**
     * 是否时最后一页
     */
    private boolean last;
    /**
     * 数量
     */
    private int number;
    /**
     * 大小
     */
    private int size;
    /**
     * 元素数量
     */
    private int numberOfElements;
    /**
     * 第一页？
     */
    private boolean first;
    /**
     * 空？
     */
    private boolean empty;
    /**
     * {@link Sort}
     */
    private Sort sort;
    /**
     * {@link Pageable}
     */
    private Pageable pageable;

    /**
     * 排序
     */
    @Data
    public static class Sort implements Serializable {
        /**
         * 是否排序了
         */
        private boolean sorted;
        /**
         * 未排序
         */
        private boolean unsorted;
        /**
         * 空
         */
        private boolean empty;
    }

    /**
     * 分页详情
     */
    @Data
    public static class Pageable implements Serializable {
        /**
         * {@link Sort}
         */
        private Sort sort;
        /**
         * 序号
         */
        private int offset;
        /**
         * 页码
         */
        private int pageNumber;
        /**
         * 每页数量
         */
        private int pageSize;
        /**
         * 未分页
         */
        private boolean unpaged;
        /**
         * 已分页
         */
        private boolean paged;
    }
}
