package top.itning.smpandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.RoomClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.client.http.RestModel;
import top.itning.smpandroid.entity.StudentRoomCheck;
import top.itning.smpandroid.ui.view.CustomVideoView;

/**
 * @author itning
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    @BindView(R2.id.videoview)
    CustomVideoView videoView;

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
        // 判断没有TOKEN的情况
        if ("".equals(getSharedPreferences(App.SHARED_PREFERENCES_OWN, Context.MODE_PRIVATE).getString(HttpHelper.TOKEN, "").trim())) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            HttpHelper.get(RoomClient.class)
                    .getStudentCheckInfo(0, 1)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<RestModel<Page<StudentRoomCheck>>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(RestModel<Page<StudentRoomCheck>> pageRestModel) {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
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

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
