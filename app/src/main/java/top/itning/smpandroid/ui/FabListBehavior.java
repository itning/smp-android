package top.itning.smpandroid.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Fab根据列表的状态显示或隐藏
 *
 * @author itning
 */
public class FabListBehavior extends FloatingActionButton.Behavior {

    private static final int MIN_CHANGED_DISTANCE = 30;

    public FabListBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return type == ViewCompat.TYPE_TOUCH;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if (dyConsumed > MIN_CHANGED_DISTANCE) {
            createValueAnimator(coordinatorLayout, child, false).start();
        } else if (dyConsumed < -MIN_CHANGED_DISTANCE) {
            createValueAnimator(coordinatorLayout, child, true).start();
        }
    }

    private Animator createValueAnimator(CoordinatorLayout coordinatorLayout, final View fab, boolean dismiss) {
        int distanceToDismiss = coordinatorLayout.getBottom() - fab.getBottom() + fab.getHeight();
        int end = dismiss ? 0 : distanceToDismiss;
        float start = fab.getTranslationY();
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(animation -> fab.setTranslationY((Float) animation.getAnimatedValue()));
        return animator;
    }

}