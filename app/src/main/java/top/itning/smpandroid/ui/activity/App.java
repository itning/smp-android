package top.itning.smpandroid.ui.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

/**
 * @author itning
 */
public class App extends Application {
    @Nullable
    public static SharedPreferences smpDataSharedPreferences;

    @Override
    public void onCreate() {
        smpDataSharedPreferences = getSharedPreferences("smp_data", Context.MODE_PRIVATE);
        super.onCreate();
    }

}
