package top.itning.smpandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.SecurityClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.client.http.RestModel;
import top.itning.smpandroid.ui.view.CustomVideoView;

/**
 * @author itning
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    @BindView(R2.id.videoview)
    CustomVideoView videoView;
    @Nullable
    private TextInputLayout textInputLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }


    private void initView() {
        //设置播放加载路径
        // Uri.parse("file:///android_asset/aa.mp4")
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash));
        //播放
        videoView.start();
        //完成播放
        videoView.setOnCompletionListener(mp -> nextActivity());
        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.w(TAG, "extra is " + extra);
            Toast.makeText(this, "该设备不支持 " + extra, Toast.LENGTH_LONG).show();
            nextActivity();
            return true;
        });
    }

    private void nextActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_OWN, Context.MODE_PRIVATE);
        if ("".equals(sharedPreferences.getString(HttpHelper.BASE_URL_KEY, "").trim())) {
            showSetBaseUrlAlertDialog(sharedPreferences);
            return;
        }
        // 判断没有TOKEN的情况
        if ("".equals(sharedPreferences.getString(HttpHelper.TOKEN_KEY, "").trim())) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            HttpHelper.get(SecurityClient.class)
                    .ping()
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RestModel<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(RestModel<String> pageRestModel) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (e instanceof HttpException) {
                                HttpException httpException = (HttpException) e;
                                if (httpException.code() == HttpHelper.UNAUTHORIZED) {
                                    RestModel<String> restModel = HttpHelper.getRestModelFromHttpException(httpException);
                                    if (restModel != null) {
                                        Toast.makeText(SplashActivity.this, restModel.getMsg(), Toast.LENGTH_LONG).show();
                                    }
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                    return;
                                }
                            } else {
                                Log.d(TAG, e.getClass().getName());
                                Log.d(TAG, "catch exception", e);
                            }
                            showAndToastAlertDialog(e, sharedPreferences);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private void showAndToastAlertDialog(Throwable e, SharedPreferences sharedPreferences) {
        String msg;
        if ("".equals(e.getMessage())) {
            msg = e.getClass().getName();
        } else {
            msg = e.getMessage();
        }
        Toast.makeText(SplashActivity.this, msg, Toast.LENGTH_LONG).show();
        showSetBaseUrlAlertDialog(sharedPreferences);
    }

    private void showSetBaseUrlAlertDialog(SharedPreferences sharedPreferences) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("设置")
                .setCancelable(false)
                .setView(R.layout.alert_init_base_url)
                .setNegativeButton("确定", (dialog, which) -> {
                    if (textInputLayout != null) {
                        EditText editText = textInputLayout.getEditText();
                        if (editText != null) {
                            Editable editable = editText.getText();
                            if (sharedPreferences.edit().putString(HttpHelper.BASE_URL_KEY, editable.toString()).commit()) {
                                HttpHelper.initRetrofit();
                                nextActivity();
                            }
                        }
                    }
                })
                .setPositiveButton("取消", (dialog, which) -> nextActivity())
                .show();
        textInputLayout = alertDialog.findViewById(R.id.ti_layout);
        if (textInputLayout != null) {
            textInputLayout.setCounterEnabled(true);
            EditText editText = textInputLayout.getEditText();
            if (editText != null) {
                editText.setSingleLine();
                editText.setText(sharedPreferences.getString(HttpHelper.BASE_URL_KEY, "http://"));
            }
        }
    }

    @Override
    protected void onStop() {
        videoView.stopPlayback();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
