package top.itning.smpandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.SecurityClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.entity.LoginUser;
import top.itning.smpandroid.entity.Role;
import top.itning.smpandroid.ui.view.CustomVideoView;
import top.itning.smpandroid.util.UserUtils;

/**
 * 登录
 *
 * @author itning
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R2.id.videoview)
    CustomVideoView videoView;
    @BindView(R2.id.iv_background)
    AppCompatImageView imageView;
    @BindView(R2.id.tl_username)
    TextInputLayout usernameLayout;
    @BindView(R2.id.tl_password)
    TextInputLayout passwordLayout;
    /**
     * 资源
     */
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

    /**
     * 初始化视图
     */
    private void initView() {
        passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        EditText usernameLayoutEditText = usernameLayout.getEditText();
        EditText passwordLayoutEditText = passwordLayout.getEditText();
        if (usernameLayoutEditText != null) {
            usernameLayoutEditText.setSingleLine();
            usernameLayoutEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    usernameLayout.setError(null);
                }
            });
        }
        if (passwordLayoutEditText != null) {
            passwordLayoutEditText.setSingleLine();
            passwordLayoutEditText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                    handleLoginBtn(v);
                    return true;
                }
                return false;
            });
            passwordLayoutEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    passwordLayout.setError(null);
                }
            });
        }

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
            imageView.setVisibility(View.VISIBLE);
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

    /**
     * 处理登录按钮点击事件
     *
     * @param view View
     */
    @SuppressWarnings("deprecation")
    public void handleLoginBtn(View view) {
        String username = "", password = "";
        EditText usernameLayoutEditText = usernameLayout.getEditText();
        EditText passwordLayoutEditText = passwordLayout.getEditText();
        if (usernameLayoutEditText != null) {
            if (usernameLayoutEditText.getText().length() == 0) {
                usernameLayout.setError("请输入用户名");
                return;
            }
            username = usernameLayoutEditText.getText().toString();
        }
        if (passwordLayoutEditText != null) {
            if (passwordLayoutEditText.getText().length() == 0) {
                passwordLayout.setError("请输入密码");
                return;
            }
            password = passwordLayoutEditText.getText().toString();
        }
        if ("".equals(username) || "".equals(password)) {
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在登录");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        disposable = HttpHelper.get(SecurityClient.class)
                .login(username, password)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restModel -> {
                    progressDialog.dismiss();
                    String token = restModel.getData();
                    doLogin(token);
                }, HttpHelper.ErrorInvoke
                        .get(this)
                        .before(t -> progressDialog.dismiss())
                        .orElseCode(t -> Toast.makeText(this, t.getT2().getMsg(), Toast.LENGTH_LONG).show())
                        .orElseException(t -> {
                            Log.w(TAG, "login exception", t);
                            Toast.makeText(this, t.getMessage(), Toast.LENGTH_LONG).show();
                        }));
    }

    /**
     * 登录并解析保存用户信息
     *
     * @param token TOKEN
     */
    private void doLogin(@Nullable String token) {
        Optional<LoginUser> loginUserOptional = UserUtils.getLoginUser(token);
        if (loginUserOptional.isPresent()) {
            LoginUser loginUser = loginUserOptional.get();
            if (loginUser.getRole().getId().equals(Role.STUDENT_ROLE_ID)) {
                SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_OWN, MODE_PRIVATE);
                if (sharedPreferences.edit()
                        .putString(HttpHelper.TOKEN_KEY, token)
                        .putString(HttpHelper.LOGIN_USER_NAME_KEY, loginUser.getName())
                        .commit()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "保存用户信息失败", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "请使用学生账户进行登录", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "login fail and login user is null");
            Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();
    }
}
