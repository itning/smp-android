package top.itning.smpandroid.ui.interpolator;

import android.animation.TimeInterpolator;

/**
 * 呼吸效果
 *
 * @author itning
 */
public class BraetheInterpolator implements TimeInterpolator {
    private static final BraetheInterpolator BRAETHE_INTERPOLATOR = new BraetheInterpolator();

    public static BraetheInterpolator getSingleInstance() {
        return BRAETHE_INTERPOLATOR;
    }

    private BraetheInterpolator() {
    }

    @Override
    public float getInterpolation(float input) {
        float x = 6 * input;
        float k = 1.0f / 3;
        int t = 6;
        // 控制函数周期，这里取此函数的第一个周期
        int n = 1;
        float pi = 3.1416f;
        float output = 0;

        if (x >= ((n - 1) * t) && x < ((n - (1 - k)) * t)) {
            output = (float) (0.5 * Math.sin((pi / (k * t)) * ((x - k * t / 2) - (n - 1) * t)) + 0.5);

        } else if (x >= (n - (1 - k)) * t && x < n * t) {
            output = (float) Math.pow((0.5 * Math.sin((pi / ((1 - k) * t)) * ((x - (3 - k) * t / 2) - (n - 1) * t)) + 0.5), 2);
        }
        return output;
    }
}