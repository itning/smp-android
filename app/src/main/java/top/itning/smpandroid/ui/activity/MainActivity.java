package top.itning.smpandroid.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.entity.Group;
import top.itning.smpandroid.ui.adapter.StudentGroupRecyclerViewAdapter;

/**
 * @author itning
 */
public class MainActivity extends AppCompatActivity implements StudentGroupRecyclerViewAdapter.OnItemClickListener<Group> {
    private static final String TAG = "MainActivity";
    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("MM月dd日 HH:mm E", Locale.CHINA));
    private static final int SETTING_REQUEST_CODE = 104;
    private static final int MUST_PERMISSIONS_REQUEST_CODE = 100;
    @Nullable
    private TextInputLayout textInputLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        initView();
    }

    private void initView() {
        initDateView();
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    private void initDateView() {
        Observable
                .fromCallable(() -> Objects.requireNonNull(SIMPLE_DATE_FORMAT_THREAD_LOCAL.get()).format(new Date()))
                .repeatWhen(objectObservable -> objectObservable.delay(5, TimeUnit.SECONDS))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    private TextView tv;

                    @Override
                    public void onSubscribe(Disposable d) {
                        tv = findViewById(R.id.tv_time);
                    }

                    @Override
                    public void onNext(String s) {
                        tv.setText(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initSwipeRefreshLayout() {
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(findViewById(R.id.cl_content), "已刷新", Snackbar.LENGTH_LONG).show();
            }, 4000);
        });
    }

    private void initRecyclerView() {
        RecyclerView rv = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        List<Group> list = new ArrayList<>();
        init(list);
        rv.setAdapter(new StudentGroupRecyclerViewAdapter(list, this, this));
    }

    private void init(List<Group> list) {
        list.add(new Group("高等数学（上）", "张良1", 25));
        list.add(new Group("计算机组成原理", "张良2", 30));
        list.add(new Group("软件测试", "张良3", 14));
        list.add(new Group("Java EE", "张良5", 26));
        list.add(new Group("基础英语", "张良6", 15));
        list.add(new Group("电路与电子", "张良7", 65));
        list.add(new Group("四六级英语", "张良8", 100));
        list.add(new Group("软件工程", "张良0", 10));
        list.add(new Group("计算机组成原理", "张良1", 10));
        list.add(new Group("电路与电子", "张良2", 44));
        list.add(new Group("电路与电子", "张良2", 10));
        list.add(new Group("电路与电子", "张良2", 25));
        list.add(new Group("电路与电子", "张良2", 66));
        list.add(new Group("电路与电子", "张良2", 22));
        list.add(new Group("电路与电子", "张良2", 15));
        list.add(new Group("电路与电子", "张良2", 10));
    }

    public void onShadowClick(View view) {
        switch (view.getId()) {
            case R.id.btn_room: {
                startActivity(new Intent(this, RoomActivity.class));
                break;
            }
            case R.id.btn_personal: {

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

    public void onFabClick(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("加入群组")
                .setCancelable(false)
                .setView(R.layout.alert_add_group)
                .setNegativeButton("加入", (dialog, which) -> {
                    if (textInputLayout != null) {
                        EditText editText = textInputLayout.getEditText();
                        if (editText != null) {
                            Editable editable = editText.getText();
                            Log.d(TAG, editable.toString());
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

    @Override
    public void onItemClick(View view, Group object) {
        Log.d(TAG, object.toString());
        Intent intent = new Intent(this, GroupActivity.class);
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
                        .setTitle("需要相机和外置存储权限")
                        .setMessage("请授予相机和外置存储权限")
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
}
