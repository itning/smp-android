package top.itning.smpandroid.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.RoomClient;
import top.itning.smpandroid.client.SecurityClient;
import top.itning.smpandroid.client.http.HttpHelper;

import static top.itning.smpandroid.ui.activity.RoomActivity.START_FACE_ACTIVITY_REQUEST_CODE;

/**
 * 个人中心
 *
 * @author itning
 */
public class PersonalActivity extends AppCompatActivity {
    private static final String TAG = "PersonalActivity";

    @BindView(R2.id.tb)
    MaterialToolbar toolbar;

    /**
     * 资源
     */
    private Disposable pwdDisposable;
    /**
     * 资源
     */
    private Disposable uploadFaceInfoDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        initToolBar();
    }

    /**
     * 初始化工具栏
     */
    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 注销登录按钮事件处理
     *
     * @param view View
     */
    public void handleLogoutBtnClick(View view) {
        if (getSharedPreferences(App.SHARED_PREFERENCES_OWN, Context.MODE_PRIVATE).edit().remove(HttpHelper.TOKEN_KEY).commit()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    /**
     * 修改密码按钮事件处理
     *
     * @param view View
     */
    public void handleChangePwdBtnClick(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View newPwdView = getLayoutInflater().inflate(R.layout.alert_change_pwd, null);
        TextInputLayout textInputLayout = newPwdView.findViewById(R.id.ti_layout);
        EditText editText = textInputLayout.getEditText();
        if (editText != null) {
            editText.setSingleLine();
            editText.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editText.getText().length() == 0 || "".contentEquals(editText.getText())) {
                        textInputLayout.setError("请输入新密码");
                        return false;
                    }
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                    doChangePwd(editText.getText().toString(), bottomSheetDialog);
                    return true;
                }
                return false;
            });
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    textInputLayout.setError(null);
                }
            });
        }
        bottomSheetDialog.setContentView(newPwdView);
        bottomSheetDialog.show();
    }

    /**
     * 发起修改密码网络请求
     *
     * @param pwd               新密码
     * @param bottomSheetDialog BottomSheetDialog
     */
    @SuppressWarnings("deprecation")
    private void doChangePwd(String pwd, BottomSheetDialog bottomSheetDialog) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("修改中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        pwdDisposable = HttpHelper.get(SecurityClient.class)
                .changePassword(pwd)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    bottomSheetDialog.dismiss();
                    progressDialog.dismiss();
                    Toast.makeText(this, "修改成功，请重新登录", Toast.LENGTH_LONG).show();
                    handleLogoutBtnClick(null);
                }, HttpHelper.ErrorInvoke.get(this)
                        .before(t -> progressDialog.dismiss())
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Toast.makeText(this, "网络请求错误", Toast.LENGTH_LONG).show();
                        }));

    }

    @Override
    protected void onDestroy() {
        if (pwdDisposable != null && !pwdDisposable.isDisposed()) {
            pwdDisposable.dispose();
        }
        if (uploadFaceInfoDisposable != null && !uploadFaceInfoDisposable.isDisposed()) {
            uploadFaceInfoDisposable.dispose();
        }
        super.onDestroy();
    }

    /**
     * 注册人脸按钮点击事件处理
     *
     * @param view View
     */
    public void handleRegisterFaceBtnClick(View view) {
        startActivityForResult(new Intent(this, FaceActivity.class), START_FACE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == START_FACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String pathName = data.getStringExtra("pathName");
                    if (pathName != null) {
                        File file = new File(pathName);
                        if (file.exists() && file.canRead() && file.isFile()) {
                            Log.d(TAG, file.getPath());
                            uploadFaceInfo(file);
                            return;
                        }
                    }
                }
                Toast.makeText(this, "注册失败", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "取消注册", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传人脸数据
     *
     * @param file 文件
     */
    @SuppressWarnings("deprecation")
    private void uploadFaceInfo(File file) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在上传数据");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        uploadFaceInfoDisposable = HttpHelper.get(RoomClient.class)
                .registerFace(part)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(studentRoomCheckRestModel -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "注册成功", Toast.LENGTH_LONG).show();
                }, HttpHelper.ErrorInvoke.get(this)
                        .before(t -> progressDialog.dismiss())
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Toast.makeText(this, "网络请求错误", Toast.LENGTH_LONG).show();
                        }));
    }
}
