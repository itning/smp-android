package top.itning.smpandroid.ui.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import top.itning.smpandroid.client.http.HttpHelper;

/**
 * @author itning
 */
public class App extends Application {
    public static final String SHARED_PREFERENCES_OWN = "smp_data";
    public static boolean needRefreshStudentClassUserData = false;

    @Nullable
    public static SharedPreferences smpDataSharedPreferences;

    @Override
    public void onCreate() {
        smpDataSharedPreferences = getSharedPreferences(SHARED_PREFERENCES_OWN, Context.MODE_PRIVATE);
        HttpHelper.initRetrofit();
        super.onCreate();
    }

}
