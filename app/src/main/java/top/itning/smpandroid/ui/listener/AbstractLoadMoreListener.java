package top.itning.smpandroid.ui.listener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * 上拉加载更多
 *
 * @author itning
 */
public abstract class AbstractLoadMoreListener extends RecyclerView.OnScrollListener {
    private int countItem;
    private int lastItem;
    /**
     * 是否可以滑动
     */
    private boolean isScolled = false;

    /**
     * 加载回调方法
     *
     * @param countItem 总数量
     * @param lastItem  最后显示的position
     */
    protected abstract void onLoading(int countItem, int lastItem);

    @Override
    public void onScrollStateChanged(@NotNull RecyclerView recyclerView, int newState) {
        //拖拽或者惯性滑动时isScolled设置为true
        isScolled = newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            countItem = layoutManager.getItemCount();
            lastItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
        }
        if (isScolled && countItem != lastItem && lastItem == countItem - 1) {
            onLoading(countItem, lastItem);
        }
    }
}
