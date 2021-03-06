package top.itning.smpandroid.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.entity.StudentRoomCheck;
import top.itning.smpandroid.ui.adapter.StudentRoomCheckRecyclerViewAdapter;
import top.itning.smpandroid.ui.interpolator.BraetheInterpolator;
import top.itning.smpandroid.ui.listener.AbstractLoadMoreListener;
import top.itning.smpandroid.util.DateUtils;
import top.itning.smpandroid.util.PageUtils;

/**
 * 寝室
 *
 * @author itning
 */
public class RoomActivity extends AppCompatActivity {
    private static final String TAG = "RoomActivity";
    /**
     * 人脸识别Activity请求码
     */
    public static final int START_FACE_ACTIVITY_REQUEST_CODE = 105;
    /**
     * 数字格式化
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.000");
    /**
     * 高德地图客户端实例
     */
    private AMapLocationClient locationClient = null;
    @BindView(R2.id.tv_address)
    AppCompatTextView addressTextView;
    @BindView(R2.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.recycler_view)
    RecyclerView rv;
    @BindView(R2.id.tb)
    MaterialToolbar toolbar;
    @BindView(R2.id.sv)
    ShadowView shadowView;
    @BindView(R2.id.cl_content)
    CoordinatorLayout coordinatorLayout;
    @BindView(R2.id.tv_last_check_time)
    TextView lastCheckTimeTextView;
    /**
     * 学生寝室打卡信息集合
     */
    private List<StudentRoomCheck> studentRoomCheckList;
    /**
     * 学生寝室打卡信息分页
     */
    @Nullable
    private Page<StudentRoomCheck> studentRoomCheckPage;
    /**
     * 资源
     */
    @Nullable
    private Disposable disposable;

    /**
     * 资源
     */
    @Nullable
    private Disposable allowCheckDisposable;
    /**
     * 资源
     */
    @Nullable
    private Disposable uploadCheckInfoDisposable;
    /**
     * 经度
     */
    private double longitude = 0;
    /**
     * 纬度
     */
    private double latitude = 0;
    /**
     * 动画
     */
    private ObjectAnimator alphaAnimator1;
    /**
     * 动画
     */
    private ObjectAnimator alphaAnimator2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        ButterKnife.bind(this);
        initView();
        initLocation();
    }

    /**
     * 初始化地理信息
     */
    private void initLocation() {
        AMapLocationClient.setApiKey("d4be613647d43ff91487e2ef7d11ce79");
        locationClient = new AMapLocationClient(getApplicationContext());
        // 设置定位回调监听
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

    /**
     * 初始化视图
     */
    private void initView() {
        initToolBar();
        initSwipeRefreshLayout();
        initRecyclerView();
        initShadowViewAnimator();
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

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        studentRoomCheckList = new ArrayList<>();
        rv.setAdapter(new StudentRoomCheckRecyclerViewAdapter(studentRoomCheckList, this));
        rv.clearOnScrollListeners();
        rv.addOnScrollListener(new AbstractLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                PageUtils.getNextPageAndSize(studentRoomCheckPage, t -> initRecyclerViewData(false, t.getT1(), t.getT2()));
            }
        });
        initRecyclerViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE);
    }

    /**
     * 初始化RecyclerView数据
     *
     * @param clear 清理集合
     * @param page  页码
     * @param size  每页数量
     */
    private void initRecyclerViewData(boolean clear, @Nullable Integer page, @Nullable Integer size) {
        swipeRefreshLayout.setRefreshing(true);
        disposable = HttpHelper
                .get(RoomClient.class)
                .getStudentCheckInfo(page, size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    if (pageRestModel.getData().getContent() == null) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    if (clear) {
                        studentRoomCheckList.clear();
                    }
                    if (!pageRestModel.getData().getContent().isEmpty()) {
                        studentRoomCheckPage = pageRestModel.getData();
                        setLastCheckTimeTextView(page, pageRestModel.getData().getContent().get(0));
                        studentRoomCheckList.addAll(pageRestModel.getData().getContent());
                    }
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
     * 设置最后寝室打卡新
     *
     * @param page             页码
     * @param studentRoomCheck 学生寝室打卡
     */
    private void setLastCheckTimeTextView(@Nullable Integer page, StudentRoomCheck studentRoomCheck) {
        if (page == null || page == 0) {
            lastCheckTimeTextView.setText(DateUtils.format(studentRoomCheck.getCheckTime(), DateUtils.YYYYMMDDHHMM_DATE_TIME_FORMATTER_3));
        }
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
     * 初始化动画
     */
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (allowCheckDisposable != null && !allowCheckDisposable.isDisposed()) {
            allowCheckDisposable.dispose();
        }
        if (uploadCheckInfoDisposable != null && !uploadCheckInfoDisposable.isDisposed()) {
            uploadCheckInfoDisposable.dispose();
        }
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
        rv.clearOnScrollListeners();
        super.onBackPressed();
    }

    /**
     * 寝室打卡按钮点击事件处理
     *
     * @param view View
     */
    public void onRoomCheckClick(View view) {
        allowCheckDisposable = HttpHelper.get(RoomClient.class)
                .allowCheck()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(booleanRestModel -> {
                    if (booleanRestModel.getData() == null || !booleanRestModel.getData()) {
                        Snackbar.make(coordinatorLayout, "还没有到打卡时间", Snackbar.LENGTH_LONG).show();
                    } else {
                        startActivityForResult(new Intent(this, FaceActivity.class), START_FACE_ACTIVITY_REQUEST_CODE);
                    }
                }, HttpHelper.ErrorInvoke.get(this)
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Snackbar.make(coordinatorLayout, "网络请求错误", Snackbar.LENGTH_LONG).show();
                        }));
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
                            uploadCheckInfo(file);
                            return;
                        }
                    }
                }
                Snackbar.make(coordinatorLayout, "打卡失败", Snackbar.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Snackbar.make(coordinatorLayout, "取消打卡", Snackbar.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 上传打卡信息
     *
     * @param file 文件
     */
    @SuppressWarnings("deprecation")
    private void uploadCheckInfo(@NonNull File file) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在上传数据");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        RequestBody body = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        uploadCheckInfoDisposable = HttpHelper.get(RoomClient.class)
                .check(part, longitude, latitude)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(studentRoomCheckRestModel -> {
                    studentRoomCheckList.add(0, studentRoomCheckRestModel.getData());
                    if (rv.getAdapter() != null) {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                    setLastCheckTimeTextView(0, studentRoomCheckRestModel.getData());
                    progressDialog.dismiss();
                    Snackbar.make(coordinatorLayout, "打卡成功", Snackbar.LENGTH_LONG).show();
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
}
