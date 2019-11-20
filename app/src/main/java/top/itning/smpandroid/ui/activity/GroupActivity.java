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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.loopeer.shadow.ShadowView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import top.itning.smpandroid.R;
import top.itning.smpandroid.entity.Group;
import top.itning.smpandroid.entity.StudentGroupCheck;
import top.itning.smpandroid.ui.adapter.StudentGroupCheckRecylerViewAdapter;
import top.itning.smpandroid.ui.interpolator.BraetheInterpolator;

/**
 * @author itning
 */
public class GroupActivity extends AppCompatActivity {
    private final static String TAG = "GroupActivity";

    @Nullable
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        group = (Group) getIntent().getSerializableExtra("data");
        initView();

        Log.d(TAG, group == null ? "null" : group.toString());
    }

    private void initView() {
        initToolBar();
        initSwipeRefreshLayout();
        initRecyclerView();
        initShadowViewAnimator();
    }

    private void initToolBar() {
        MaterialToolbar toolbar = findViewById(R.id.tb);
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
        RecyclerView rv = findViewById(R.id.recycler_view);
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

    private void initShadowViewAnimator() {
        ShadowView shadowView = findViewById(R.id.sv);
        int c1 = ContextCompat.getColor(this, R.color.class_color_1);
        int c2 = ContextCompat.getColor(this, R.color.class_color_2);
        int c6 = ContextCompat.getColor(this, R.color.class_color_6);
        int c7 = ContextCompat.getColor(this, R.color.class_color_7);
        ObjectAnimator alphaAnimator1 = ObjectAnimator.ofArgb(shadowView, "backgroundClr", c1, c2, c6, c7);
        alphaAnimator1.setDuration(10000);
        //使用自定义的插值器
        alphaAnimator1.setInterpolator(BraetheInterpolator.getSingleInstance());
        alphaAnimator1.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator alphaAnimator2 = ObjectAnimator.ofArgb(shadowView, "shadowColor", c1, c2, c6, c7);
        alphaAnimator2.setDuration(10000);
        //使用自定义的插值器
        alphaAnimator2.setInterpolator(BraetheInterpolator.getSingleInstance());
        alphaAnimator2.setRepeatCount(ValueAnimator.INFINITE);

        alphaAnimator1.start();
        alphaAnimator2.start();
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

    }
}
