package top.itning.smpandroid.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;

import top.itning.smpandroid.R;

/**
 * 小圆点
 *
 * @author itning
 */
public class RoundBackChange extends View {
    private int color = -0x22000001;
    private Paint mPaint = new Paint();

    public RoundBackChange(Context context) {
        super(context);
    }

    public RoundBackChange(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //设置画笔宽度为10px
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.RoundBackChange);
        color = array.getColor(R.styleable.RoundBackChange_self_color, color);
        array.recycle();
        //设置画笔颜色
        mPaint.setColor(color);
        //设置画笔模式为填充
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(10f);
        mPaint.setAntiAlias(true);
    }

    public void setBackColor(@ColorInt int color) {
        this.color = color;
        mPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawCircle((float) (this.getRight() - this.getLeft() - this.getMeasuredWidth() / 2), (float) (getTop() + getMeasuredHeight() / 2), (float) (getMeasuredWidth() / 3), mPaint);
    }
}
