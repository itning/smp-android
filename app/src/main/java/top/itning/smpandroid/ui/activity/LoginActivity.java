package top.itning.smpandroid.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.LoginClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.ui.view.CustomVideoView;

/**
 * @author itning
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R2.id.videoview)
    CustomVideoView videoView;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
    }

    private void initView() {
        //设置播放加载路径
        // Uri.parse("file:///android_asset/aa.mp4")
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login));
        //播放
        videoView.start();
        //循环播放
        videoView.setOnCompletionListener(mp -> videoView.start());
        videoView.setOnErrorListener((mp, what, extra) -> {
            Log.w(TAG, "extra is " + extra);
            Toast.makeText(this, "该设备不支持 " + extra, Toast.LENGTH_LONG).show();
            return true;
        });
    }

    @Override
    protected void onRestart() {
        initView();
        super.onRestart();
    }

    @Override
    protected void onStop() {
        videoView.stopPlayback();
        super.onStop();
    }

    public void handleLoginBtn(View view) {
        disposable = HttpHelper.get(LoginClient.class)
                .login("a", "a")
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restModel -> {
                    String token = restModel.getData();
                    if (token != null && !"".equals(token.trim())) {
                        SharedPreferences sharedPreferences = getSharedPreferences("smp_data", MODE_PRIVATE);
                        if (sharedPreferences.edit().putString("token", token).commit()) {
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }
                    }
                }, throwable -> {
                    Log.w(TAG, "登陆失败", throwable);
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
