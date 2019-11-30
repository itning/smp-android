package top.itning.smpandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.util.Date;

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
import top.itning.smpandroid.entity.Wrap;
import top.itning.smpandroid.ui.view.CustomVideoView;

/**
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
                    String token = restModel.getData();
                    if (token != null && !"".equals(token.trim())) {
                        SharedPreferences sharedPreferences = getSharedPreferences(App.SHARED_PREFERENCES_OWN, MODE_PRIVATE);
                        if (sharedPreferences.edit().putString(HttpHelper.TOKEN_KEY, token).commit()) {
                            setUserInfo(sharedPreferences);
                            progressDialog.dismiss();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                            return;
                        }
                    }
                    progressDialog.dismiss();
                    Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
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
     * 登陆成功后设置用户信息
     *
     * @param preferences SharedPreferences
     */
    private void setUserInfo(@NonNull SharedPreferences preferences) {
        try {
            String json = new String(Base64.decode(preferences.getString(HttpHelper.TOKEN_KEY, "").split("\\.")[1], Base64.URL_SAFE));
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json1, type, context) -> new Date(json1.getAsLong())).create();
            Wrap wrap = gson.fromJson(json, Wrap.class);
            LoginUser loginUser = gson.fromJson(wrap.getLoginUser(), LoginUser.class);
            if (preferences.edit().putString(HttpHelper.LOGIN_USER_NAME_KEY, loginUser.getName()).commit()) {
                Log.i(TAG, loginUser.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
            Toast.makeText(this, "保存用户信息失败", Toast.LENGTH_LONG).show();
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
