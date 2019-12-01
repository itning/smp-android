package top.itning.smpandroid.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.ClassClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.entity.StudentClassUser;
import top.itning.smpandroid.ui.adapter.StudentClassUserRecyclerViewAdapter;
import top.itning.smpandroid.ui.listener.AbstractLoadMoreListener;
import top.itning.smpandroid.util.DateUtils;
import top.itning.smpandroid.util.PageUtils;
import top.itning.smpandroid.util.Tuple2;

import static top.itning.smpandroid.util.DateUtils.MMDDHHMME_DATE_TIME_FORMATTER_4;
import static top.itning.smpandroid.util.DateUtils.ZONE_ID;

/**
 * 主活动
 *
 * @author itning
 */
public class MainActivity extends AppCompatActivity implements StudentClassUserRecyclerViewAdapter.OnItemClickListener<StudentClassUser> {
    private static final String TAG = "MainActivity";
    /**
     * 应用设置界面请求码
     */
    private static final int SETTING_REQUEST_CODE = 104;
    /**
     * 必须权限申请请求码
     */
    private static final int MUST_PERMISSIONS_REQUEST_CODE = 100;
    @BindView(R2.id.tv_hello)
    TextView helloTextView;
    @BindView(R2.id.tv_time)
    TextView tv;
    @BindView(R2.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.cl_content)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.recycler_view)
    RecyclerView rv;
    /**
     * 文本输入布局实例
     */
    @Nullable
    private TextInputLayout textInputLayout = null;
    /**
     * 资源
     */
    @Nullable
    private Disposable titleDisposable;
    /**
     * 资源
     */
    @Nullable
    private Disposable recyclerViewDataDisposable;
    /**
     * 资源
     */
    private Disposable joinDisposable;
    /**
     * 学生班级用户集合
     */
    private List<StudentClassUser> studentClassUserList;
    /**
     * 学生班级用户分页
     */
    private Page<StudentClassUser> studentClassUserPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        checkPermissions();
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        initTitleView();
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    /**
     * 初始化标题
     */
    private void initTitleView() {
        final SharedPreferences preferences = getSharedPreferences(App.SHARED_PREFERENCES_OWN, Context.MODE_PRIVATE);
        titleDisposable = Observable
                .fromCallable(() -> new Tuple2<>(LocalDateTime.now(ZONE_ID).format(MMDDHHMME_DATE_TIME_FORMATTER_4),
                        DateUtils.helloTime(preferences.getString(HttpHelper.LOGIN_USER_NAME_KEY, null))))
                .repeatWhen(objectObservable -> objectObservable.delay(5, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    tv.setText(s.getT1());
                    helloTextView.setText(s.getT2());
                }, throwable -> Log.e(TAG, "title view error", throwable));
    }

    /**
     * 初始化下拉刷新
     */
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(() -> initRecyclerViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE));
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        studentClassUserList = new ArrayList<>();
        rv.setAdapter(new StudentClassUserRecyclerViewAdapter(studentClassUserList, this, this));
        rv.clearOnScrollListeners();
        rv.addOnScrollListener(new AbstractLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                PageUtils.getNextPageAndSize(studentClassUserPage, t -> initRecyclerViewData(false, t.getT1(), t.getT2()));
            }
        });
        initRecyclerViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE);
    }

    /**
     * 初始化RecyclerView数据
     *
     * @param clear 清理集合
     * @param page  分页
     * @param size  数量
     */
    private void initRecyclerViewData(boolean clear, @Nullable Integer page, @Nullable Integer size) {
        swipeRefreshLayout.setRefreshing(true);
        recyclerViewDataDisposable = HttpHelper.get(ClassClient.class)
                .getAllStudentClassUser(page, size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    if (pageRestModel.getData().getContent() == null) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    if (clear) {
                        studentClassUserList.clear();
                    }
                    studentClassUserPage = pageRestModel.getData();
                    studentClassUserList.addAll(pageRestModel.getData().getContent());
                    if (rv.getAdapter() != null) {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }, HttpHelper.ErrorInvoke.get(this)
                        .before(t -> swipeRefreshLayout.setRefreshing(false))
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Snackbar.make(coordinatorLayout, "网络请求错误", Snackbar.LENGTH_LONG).show();
                        }));

    }

    /**
     * 按钮点击事件处理
     *
     * @param view View
     */
    public void onShadowClick(View view) {
        switch (view.getId()) {
            case R.id.btn_room: {
                startActivity(new Intent(this, RoomActivity.class));
                break;
            }
            case R.id.btn_personal: {
                startActivity(new Intent(this, PersonalActivity.class));
                break;
            }
            case R.id.btn_leave: {
                startActivity(new Intent(this, LeaveActivity.class));
                break;
            }
            default:
        }
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        String[] ps = Stream.of
                (
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .filter(permission -> ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                .toArray(String[]::new);
        if (ps.length != 0) {
            ActivityCompat.requestPermissions(this, ps, MUST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * 加入班级
     *
     * @param view View
     */
    public void onJoinClassClick(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("加入班级")
                .setCancelable(false)
                .setView(R.layout.alert_join_class)
                .setNegativeButton("加入", (dialog, which) -> {
                    if (textInputLayout != null) {
                        EditText editText = textInputLayout.getEditText();
                        if (editText != null) {
                            if (editText.getText().length() == 0 || "".contentEquals(editText.getText())) {
                                Snackbar.make(coordinatorLayout, "请输入班号", Snackbar.LENGTH_LONG).show();
                                return;
                            }
                            Editable editable = editText.getText();
                            Log.d(TAG, editable.toString());
                            doJoinClass(editable.toString());
                        }
                    }
                })
                .setPositiveButton("取消", null)
                .show();
        textInputLayout = alertDialog.findViewById(R.id.ti_layout);
        if (textInputLayout != null) {
            textInputLayout.setCounterEnabled(true);
            EditText editText = textInputLayout.getEditText();
            if (editText != null) {
                editText.setSingleLine();
            }
        }
    }

    /**
     * 网络请求加入班级
     *
     * @param classNum 班号
     */
    private void doJoinClass(String classNum) {
        joinDisposable = HttpHelper.get(ClassClient.class)
                .joinClass(classNum)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    studentClassUserList.add(0, pageRestModel.getData());
                    if (rv.getAdapter() != null) {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                }, HttpHelper.ErrorInvoke.get(this)
                        .orElseCode(t -> Snackbar.make(coordinatorLayout, t.getT2() == null ? t.getT1().code() + "" : t.getT2().getMsg(), Snackbar.LENGTH_LONG).show())
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Snackbar.make(coordinatorLayout, "网络请求错误", Snackbar.LENGTH_LONG).show();
                        }));
    }

    @Override
    public void onItemClick(View view, StudentClassUser object) {
        Intent intent = new Intent(this, ClassCheckActivity.class);
        intent.putExtra("data", object);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MUST_PERMISSIONS_REQUEST_CODE) {
            boolean granted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                }
            }
            if (granted) {
                checkPermissions();
            } else {
                new AlertDialog
                        .Builder(this)
                        .setTitle("需要权限")
                        .setMessage("请授予权限")
                        .setCancelable(false)
                        .setPositiveButton("确定", (dialog, which) -> startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null)), SETTING_REQUEST_CODE))
                        .show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE) {
            checkPermissions();
        }
    }

    @Override
    public void onBackPressed() {
        if (titleDisposable != null && !titleDisposable.isDisposed()) {
            titleDisposable.dispose();
        }
        if (recyclerViewDataDisposable != null && !recyclerViewDataDisposable.isDisposed()) {
            recyclerViewDataDisposable.dispose();
        }
        if (joinDisposable != null && !joinDisposable.isDisposed()) {
            joinDisposable.dispose();
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if (App.needRefreshStudentClassUserData) {
            App.needRefreshStudentClassUserData = false;
            initRecyclerViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE);
        }
        super.onResume();
    }
}
