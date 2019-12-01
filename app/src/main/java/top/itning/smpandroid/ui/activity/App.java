package top.itning.smpandroid.ui.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import top.itning.smpandroid.client.http.HttpHelper;

/**
 * 应用实例
 *
 * @author itning
 */
public class App extends Application {
    /**
     * shared_preferences xml name
     */
    public static final String SHARED_PREFERENCES_OWN = "smp_data";
    /**
     * 当B Activity销毁时是否刷新A中数据
     */
    public static boolean needRefreshStudentClassUserData = false;
    /**
     * shared_preferences 实例
     */
    @Nullable
    public static SharedPreferences smpDataSharedPreferences;

    @Override
    public void onCreate() {
        smpDataSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_OWN, Context.MODE_PRIVATE);
        HttpHelper.initRetrofit();
        super.onCreate();
    }

}
