package top.itning.smpandroid.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

import top.itning.smpandroid.client.http.Page;

/**
 * @author itning
 */
public class PageUtils {
    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 0;
    /**
     * 默认每页数量
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * 获取下一页
     * 只有当前不是最后一页和pageInfo参数不为null时调用consumer
     *
     * @param pageInfo 分页信息
     * @param consumer 下一页回调
     * @param <T>      分页信息泛型
     */
    public static <T> void getNextPageAndSize(@Nullable Page<T> pageInfo, @NonNull Consumer<Tuple2<Integer, Integer>> consumer) {
        if (pageInfo != null && !pageInfo.isLast()) {
            consumer.accept(new Tuple2<>(pageInfo.getNumber() + 1, pageInfo.getSize()));
        }
    }
}
