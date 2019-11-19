package top.itning.smpandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import top.itning.smpandroid.entity.Group;
import top.itning.smpandroid.ui.adapter.StudentGroupRecyclerViewAdapter;

/**
 * @author itning
 */
public class MainActivity extends AppCompatActivity implements StudentGroupRecyclerViewAdapter.OnItemClickListener<Group> {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
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

    }

    public void onFabClick(View view) {

    }

    @Override
    public void onItemClick(View view, Group object) {
        Log.d(TAG, object.toString());
        CoordinatorLayout coordinatorLayout = findViewById(R.id.cl_content);
        Snackbar.make(coordinatorLayout, object.toString(), Snackbar.LENGTH_LONG).show();
    }
}
