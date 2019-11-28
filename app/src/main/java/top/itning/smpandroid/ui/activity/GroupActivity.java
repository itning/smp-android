package top.itning.smpandroid.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.entity.Group;
import top.itning.smpandroid.entity.StudentGroupCheck;
import top.itning.smpandroid.ui.adapter.StudentGroupCheckRecylerViewAdapter;
import top.itning.smpandroid.ui.interpolator.BraetheInterpolator;

/**
 * @author itning
 */
public class GroupActivity extends AppCompatActivity {
    private final static String TAG = "GroupActivity";
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
    @Nullable
    private Group group;
    private ObjectAnimator alphaAnimator1;
    private ObjectAnimator alphaAnimator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        group = (Group) getIntent().getSerializableExtra("data");
        initView();
        initLocation();
        Log.d(TAG, group == null ? "null" : group.toString());
    }

    private void initLocation() {
        AMapLocationClient.setApiKey("d4be613647d43ff91487e2ef7d11ce79");
        locationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        locationClient.setLocationListener(aMapLocation -> {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
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
        locationOption.setMockEnable(false);
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
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.item_exit_group) {
                Log.d(TAG, "退出群组");
                return true;
            }
            return false;
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        List<StudentGroupCheck> list = new ArrayList<>();
        init(list);
        rv.setAdapter(new StudentGroupCheckRecylerViewAdapter(list, this));
    }

    private void init(List<StudentGroupCheck> list) {
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
        list.add(new StudentGroupCheck(new Date()));
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(coordinatorLayout, "已刷新", Snackbar.LENGTH_LONG).show();
        }, 4000));
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
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
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
        Snackbar.make(coordinatorLayout, "老师没有开启签到", Snackbar.LENGTH_LONG).show();
    }
}
