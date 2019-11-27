package top.itning.smpandroid.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.client.LeaveClient;
import top.itning.smpandroid.client.http.HttpHelper;
import top.itning.smpandroid.client.http.Page;
import top.itning.smpandroid.entity.Leave;
import top.itning.smpandroid.entity.LeaveReason;
import top.itning.smpandroid.ui.adapter.StudentLeaveReasonRecyclerViewAdapter;
import top.itning.smpandroid.ui.adapter.StudentLeaveRecyclerViewAdapter;
import top.itning.smpandroid.ui.listener.AbstractLoadMoreListener;
import top.itning.smpandroid.util.PageUtils;

/**
 * @author itning
 */
public class LeaveActivity extends AppCompatActivity implements StudentLeaveRecyclerViewAdapter.OnItemClickListener<Leave> {
    private static final String TAG = "LeaveActivity";

    @BindView(R2.id.tb)
    MaterialToolbar toolbar;
    @BindView(R2.id.srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.recycler_view)
    RecyclerView rv;
    @BindView(R2.id.cl_content)
    CoordinatorLayout coordinatorLayout;
    private List<Leave> leaveList;
    @Nullable
    private Page<Leave> leavePage;
    @Nullable
    private Disposable disposable;
    private Disposable newReasonDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initToolBar();
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        leaveList = new ArrayList<>();
        rv.setAdapter(new StudentLeaveRecyclerViewAdapter(leaveList, this, this));
        rv.clearOnScrollListeners();
        rv.addOnScrollListener(new AbstractLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                PageUtils.getNextPageAndSize(leavePage, t -> initRecyleViewData(false, t.getT1(), t.getT2()));
            }
        });
        initRecyleViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE);
    }

    private void initRecyleViewData(boolean clear, @Nullable Integer page, @Nullable Integer size) {
        swipeRefreshLayout.setRefreshing(true);
        disposable = HttpHelper
                .get(LeaveClient.class)
                .getStudentLeaves(page, size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    if (pageRestModel.getData().getContent() == null || pageRestModel.getData().getContent().isEmpty()) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    if (clear) {
                        leaveList.clear();
                    }
                    leavePage = pageRestModel.getData();
                    leaveList.addAll(pageRestModel.getData().getContent());
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

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(() -> initRecyleViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE));
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
    public void onBackPressed() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (newReasonDisposable != null && !newReasonDisposable.isDisposed()) {
            newReasonDisposable.dispose();
        }
        rv.clearOnScrollListeners();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFabClick(View view) {
        Log.d(TAG, "新增请假");
    }

    @Override
    public void onItemClick(View view, Leave leave) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View leaveReasonView = getLayoutInflater().inflate(R.layout.alert_leave_reason, null);
        RecyclerView recyclerView = leaveReasonView.findViewById(R.id.recycler_view);
        TextInputLayout textInputLayout = leaveReasonView.findViewById(R.id.ti_layout);
        TextView reasonTextView = leaveReasonView.findViewById(R.id.tv_reason);
        String reason = "原因：" + leave.getReason();
        reasonTextView.setText(reason);
        initReason(recyclerView, textInputLayout, leave);
        dialog.setContentView(leaveReasonView);
        dialog.show();
    }

    private void initReason(RecyclerView recyclerView, TextInputLayout textInputLayout, Leave leave) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new StudentLeaveReasonRecyclerViewAdapter(leave.getLeaveReasonList(), this));
        if (leave.getStatus() == null) {
            EditText editText = textInputLayout.getEditText();
            if (editText != null) {
                editText.setSingleLine();
                editText.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputMethodManager != null) {
                            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        }
                        handleNewReasonBtn(recyclerView.getAdapter(), textInputLayout, editText, leave);
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
        } else {
            textInputLayout.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    private void handleNewReasonBtn(@Nullable RecyclerView.Adapter adapter, TextInputLayout textInputLayout, EditText editText, Leave leave) {
        if (editText.getText().length() == 0 || "".contentEquals(editText.getText())) {
            textInputLayout.setError("请输入评论");
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        newReasonDisposable = HttpHelper.get(LeaveClient.class)
                .newComment(leave.getId(), editText.getText().toString())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restModel -> {
                    progressDialog.dismiss();
                    textInputLayout.clearFocus();
                    editText.clearFocus();
                    editText.setText("");
                    List<LeaveReason> leaveReasonList = leave.getLeaveReasonList();
                    leaveReasonList.add(0, restModel.getData());
                    leave.setLeaveReasonList(leaveReasonList);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }, HttpHelper.ErrorInvoke.get(this)
                        .before(t -> progressDialog.dismiss())
                        .orElseCode(t -> Toast.makeText(this, t.getT2().getMsg(), Toast.LENGTH_LONG).show())
                        .orElseException(t -> {
                            Log.w(TAG, "网络请求错误", t);
                            Toast.makeText(this, "网络请求错误", Toast.LENGTH_LONG).show();
                        }));
    }
}
