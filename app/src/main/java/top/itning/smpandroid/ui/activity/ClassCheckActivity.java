package top.itning.smpandroid.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.loopeer.shadow.ShadowView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.ClassClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.entity.StudentClassCheck;
import top.itning.smpandroid.entity.StudentClassUser;
import top.itning.smpandroid.ui.adapter.StudentClassCheckRecyclerViewAdapter;
import top.itning.smpandroid.ui.interpolator.BraetheInterpolator;
import top.itning.smpandroid.ui.listener.AbstractLoadMoreListener;
import top.itning.smpandroid.util.DateUtils;
import top.itning.smpandroid.util.PageUtils;

/**
 * @author itning
 */
public class ClassCheckActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {
    private final static String TAG = "ClassCheckActivity";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.000");
    private AMapLocationClient locationClient = null;
    @BindView(R2.id.tv_address)
    AppCompatTextView addressTextView;
    @BindView(R2.id.tb)
    MaterialToolbar toolbar;
    @BindView(R2.id.recycler_view)
    RecyclerView rv;
    @BindView(R2.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.cl_content)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.sv)
    ShadowView shadowView;
    @BindView(R2.id.tv_last_check_time)
    TextView lastCheckTimeTextView;
    @Nullable
    private StudentClassUser studentClassUserFromIntent;
    private ObjectAnimator alphaAnimator1;
    private ObjectAnimator alphaAnimator2;
    private List<StudentClassCheck> studentClassCheckList;
    private Page<StudentClassCheck> studentClassCheckPage;
    @Nullable
    private Disposable initRecyclerViewDataDisposable;
    @Nullable
    private Disposable canCheckDisposable;
    @Nullable
    private Disposable checkDisposable;
    private double longitude = 0;
    private double latitude = 0;
    private Disposable quitClassDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_check);
        ButterKnife.bind(this);
        studentClassUserFromIntent = (StudentClassUser) getIntent().getSerializableExtra("data");
        initView();
        initLocation();
    }

    private void initLocation() {
        AMapLocationClient.setApiKey("d4be613647d43ff91487e2ef7d11ce79");
        locationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        locationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    longitude = aMapLocation.getLongitude();
                    latitude = aMapLocation.getLatitude();
                    if (addressTextView != null) {
                        if (!"".equals(aMapLocation.getDescription())) {
                            addressTextView.setText(aMapLocation.getDescription());
                        } else {
                            StringBuilder sb = new StringBuilder()
                                    .append("经度：")
                                    .append(DECIMAL_FORMAT.format(aMapLocation.getLongitude()))
                                    .append(" 纬度：")
                                    .append(DECIMAL_FORMAT.format(aMapLocation.getLatitude()));
                            addressTextView.setText(sb);
                        }
                    }
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e(TAG, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        });
        //初始化AMapLocationClientOption对象
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        AMapLocationClientOption option = new AMapLocationClientOption();

        // 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != locationClient) {
            locationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            locationClient.stopLocation();
            locationClient.startLocation();
        }
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        locationOption.setMockEnable(true);
        //给定位客户端对象设置定位参数
        locationClient.setLocationOption(locationOption);
        //启动定位
        locationClient.startLocation();
    }

    private void initView() {
        initToolBar();
        initSwipeRefreshLayout();
        initRecyclerView();
        initShadowViewAnimator();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setOnMenuItemClickListener(this);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        studentClassCheckList = new ArrayList<>();
        rv.setAdapter(new StudentClassCheckRecyclerViewAdapter(studentClassCheckList, this));
        rv.clearOnScrollListeners();
        rv.addOnScrollListener(new AbstractLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                PageUtils.getNextPageAndSize(studentClassCheckPage, t -> initRecyclerViewData(false, t.getT1(), t.getT2()));
            }
        });
        initRecyclerViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE);
    }

    private void initRecyclerViewData(boolean clear, @Nullable Integer page, @Nullable Integer size) {
        if (studentClassUserFromIntent == null) {
            Snackbar.make(coordinatorLayout, "获取数据失败", Snackbar.LENGTH_LONG).show();
            return;
        }
        initRecyclerViewDataDisposable = HttpHelper.get(ClassClient.class)
                .getAllChecks(studentClassUserFromIntent.getStudentClass().getId(), page, size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    if (pageRestModel.getData().getContent() == null) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    if (clear) {
                        studentClassCheckList.clear();
                    }
                    studentClassCheckPage = pageRestModel.getData();
                    setLastCheckTimeTextView(page, pageRestModel.getData().getContent().get(0));
                    studentClassCheckList.addAll(pageRestModel.getData().getContent());
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

    private void setLastCheckTimeTextView(@Nullable Integer page, StudentClassCheck studentClassCheck) {
        if (page == null || page == 0) {
            lastCheckTimeTextView.setText(DateUtils.format(studentClassCheck.getCheckTime(), DateUtils.YYYYMMDDHHMM_DATE_TIME_FORMATTER_3));
        }
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(() -> initRecyclerViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE));
    }

    private void initShadowViewAnimator() {
        int c1 = ContextCompat.getColor(this, R.color.class_color_1);
        int c2 = ContextCompat.getColor(this, R.color.class_color_2);
        int c6 = ContextCompat.getColor(this, R.color.class_color_6);
        int c7 = ContextCompat.getColor(this, R.color.class_color_7);
        alphaAnimator1 = ObjectAnimator.ofArgb(shadowView, "backgroundClr", c1, c2, c6, c7);
        alphaAnimator1.setDuration(10000);
        //使用自定义的插值器
        alphaAnimator1.setInterpolator(BraetheInterpolator.getSingleInstance());
        alphaAnimator1.setRepeatCount(ValueAnimator.INFINITE);

        alphaAnimator2 = ObjectAnimator.ofArgb(shadowView, "shadowColor", c1, c2, c6, c7);
        alphaAnimator2.setDuration(10000);
        //使用自定义的插值器
        alphaAnimator2.setInterpolator(BraetheInterpolator.getSingleInstance());
        alphaAnimator2.setRepeatCount(ValueAnimator.INFINITE);

        alphaAnimator1.start();
        alphaAnimator2.start();
    }

    @Override
    protected void onPause() {
        if (alphaAnimator1 != null) {
            alphaAnimator1.pause();

        }
        if (alphaAnimator2 != null) {
            alphaAnimator2.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (alphaAnimator1 != null) {
            alphaAnimator1.resume();

        }
        if (alphaAnimator2 != null) {
            alphaAnimator2.resume();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        locationClient.stopLocation();
        locationClient.onDestroy();
        if (alphaAnimator1 != null) {
            alphaAnimator1.removeAllListeners();
            alphaAnimator1.end();
            alphaAnimator1.cancel();
        }
        if (alphaAnimator2 != null) {
            alphaAnimator2.removeAllListeners();
            alphaAnimator2.end();
            alphaAnimator2.cancel();
        }
        if (initRecyclerViewDataDisposable != null && !initRecyclerViewDataDisposable.isDisposed()) {
            initRecyclerViewDataDisposable.dispose();
        }
        if (canCheckDisposable != null && !canCheckDisposable.isDisposed()) {
            canCheckDisposable.dispose();
        }
        if (checkDisposable != null && !checkDisposable.isDisposed()) {
            checkDisposable.dispose();
        }
        if (quitClassDisposable != null && !quitClassDisposable.isDisposed()) {
            quitClassDisposable.dispose();
        }
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onShadowClick(View view) {
        if (studentClassUserFromIntent == null) {
            Snackbar.make(coordinatorLayout, "无法签到，获取班级信息失败", Snackbar.LENGTH_LONG).show();
            return;
        }
        String studentClassId = studentClassUserFromIntent.getStudentClass().getId();
        canCheckDisposable = HttpHelper.get(ClassClient.class)
                .canCheck(studentClassId)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(booleanRestModel -> {
                    if (booleanRestModel.getData()) {
                        doClassCheck();
                    } else {
                        Snackbar.make(coordinatorLayout, "教师未开启签到", Snackbar.LENGTH_LONG).show();
                    }
                }, HttpHelper.ErrorInvoke.get(this)
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Snackbar.make(coordinatorLayout, "网络请求错误", Snackbar.LENGTH_LONG).show();
                        }));
    }

    @SuppressWarnings("deprecation")
    private void doClassCheck() {
        if (studentClassUserFromIntent == null) {
            Snackbar.make(coordinatorLayout, "无法签到，获取班级信息失败", Snackbar.LENGTH_LONG).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在签到");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        checkDisposable = HttpHelper.get(ClassClient.class)
                .check(longitude, latitude, studentClassUserFromIntent.getStudentClass().getId())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(studentClassCheckRestModel -> {
                    studentClassCheckList.add(0, studentClassCheckRestModel.getData());
                    if (rv.getAdapter() != null) {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    setLastCheckTimeTextView(0, studentClassCheckRestModel.getData());
                    progressDialog.dismiss();
                    Snackbar.make(coordinatorLayout, "签到成功", Snackbar.LENGTH_LONG).show();
                }, HttpHelper.ErrorInvoke.get(this)
                        .before(t -> progressDialog.dismiss())
                        .orElseCode(t -> {
                            String msg = t.getT2() != null ? t.getT2().getMsg() : t.getT1().code() + "";
                            Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                        })
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Snackbar.make(coordinatorLayout, "网络请求错误", Snackbar.LENGTH_LONG).show();
                        }));
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.item_exit_class) {
            if (studentClassUserFromIntent == null) {
                Snackbar.make(coordinatorLayout, "班级信息异常", Snackbar.LENGTH_LONG).show();
                return false;
            }
            new AlertDialog.Builder(this)
                    .setTitle("确定退出？")
                    .setCancelable(false)
                    .setNegativeButton("确定", (dialog, which) -> {
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("请稍后");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        quitClassDisposable = HttpHelper.get(ClassClient.class)
                                .quitClass(studentClassUserFromIntent.getStudentClass().getId())
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(noContent -> {
                                    App.needRefreshStudentClassUserData = true;
                                    progressDialog.dismiss();
                                    Toast.makeText(this, "退出成功", Toast.LENGTH_LONG).show();
                                    onBackPressed();
                                }, HttpHelper.ErrorInvoke.get(this)
                                        .before(t -> progressDialog.dismiss())
                                        .orElseCode(t -> {
                                            String msg = t.getT2() != null ? t.getT2().getMsg() : t.getT1().code() + "";
                                            Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG).show();
                                        })
                                        .orElseException(t -> {
                                            Log.w(TAG, "网络请求错误", t);
                                            Snackbar.make(coordinatorLayout, "网络请求错误", Snackbar.LENGTH_LONG).show();
                                        }));
                    })
                    .setPositiveButton("取消", null)
                    .show();
            return true;
        }
        if (item.getItemId() == R.id.item_check_history && studentClassUserFromIntent != null) {
            Intent intent = new Intent(this, ClassCheckUserActivity.class);
            intent.putExtra("data", studentClassUserFromIntent);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
