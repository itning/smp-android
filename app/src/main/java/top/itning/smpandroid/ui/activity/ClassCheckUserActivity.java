package top.itning.smpandroid.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;

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
import top.itning.smpandroid.entity.StudentClassCheckDTO;
import top.itning.smpandroid.entity.StudentClassUser;
import top.itning.smpandroid.ui.adapter.StudentCheckDetailRecyclerViewDataAdapter;

/**
 * @author itning
 */
public class ClassCheckUserActivity extends AppCompatActivity {
    private static final String TAG = "ClassCheckUserActivity";

    @BindView(R2.id.tb)
    MaterialToolbar toolbar;
    @BindView(R2.id.recycler_view)
    RecyclerView rv;
    @BindView(R2.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;

    private StudentClassUser studentClassUser;
    private Disposable userCheckDetailDisposable;
    private List<StudentClassCheckDTO> studentClassCheckDtoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_check_user);
        ButterKnife.bind(this);
        studentClassUser = (StudentClassUser) getIntent().getSerializableExtra("data");
        initView();
    }

    private void initView() {
        initToolBar();
        if (studentClassUser == null) {
            Toast.makeText(this, "数据异常", Toast.LENGTH_LONG).show();
            return;
        }
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(this::initRecyclerViewData);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        studentClassCheckDtoList = new ArrayList<>();
        rv.setAdapter(new StudentCheckDetailRecyclerViewDataAdapter(this, studentClassCheckDtoList));
        initRecyclerViewData();
    }

    private void initRecyclerViewData() {
        swipeRefreshLayout.setRefreshing(true);
        userCheckDetailDisposable = HttpHelper.get(ClassClient.class)
                .getUserCheckDetail(studentClassUser.getUser().getUsername(), studentClassUser.getStudentClass().getId())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listRestModel -> {
                    swipeRefreshLayout.setRefreshing(false);
                    studentClassCheckDtoList.clear();
                    studentClassCheckDtoList.addAll(listRestModel.getData());
                    if (rv.getAdapter() != null) {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                }, HttpHelper.ErrorInvoke.get(this)
                        .before(t -> swipeRefreshLayout.setRefreshing(false))
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Toast.makeText(this, "网络请求错误", Toast.LENGTH_LONG).show();
                        }));
    }

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

    @Override
    public void onBackPressed() {
        if (userCheckDetailDisposable != null && !userCheckDetailDisposable.isDisposed()) {
            userCheckDetailDisposable.dispose();
        }
        super.onBackPressed();
    }
}
