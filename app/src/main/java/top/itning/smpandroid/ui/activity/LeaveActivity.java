package top.itning.smpandroid.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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
import top.itning.smpandroid.entity.LeaveType;
import top.itning.smpandroid.ui.adapter.StudentLeaveReasonRecyclerViewAdapter;
import top.itning.smpandroid.ui.adapter.StudentLeaveRecyclerViewAdapter;
import top.itning.smpandroid.ui.listener.AbstractLoadMoreListener;
import top.itning.smpandroid.util.DateUtils;
import top.itning.smpandroid.util.PageUtils;

import static top.itning.smpandroid.util.DateUtils.YYYYMMDD_DATE_TIME_FORMATTER_7;
import static top.itning.smpandroid.util.DateUtils.ZONE_ID;

/**
 * @author itning
 */
public class LeaveActivity extends AppCompatActivity implements StudentLeaveRecyclerViewAdapter.OnItemClickListener<Leave>, View.OnClickListener, DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener {
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
    @Nullable
    private Disposable newReasonDisposable;
    @Nullable
    private Disposable newLeaveDisposable;
    @Nullable
    private Leave leave;
    @Nullable
    private TextView startTextView;
    @Nullable
    private TextView endTextView;
    @Nullable
    private TextInputLayout newLeaveReasonViewTextInputLayout;
    @Nullable
    private BottomSheetDialog newLeaveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        initToolBar();
        initSwipeRefreshLayout();
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        leaveList = new ArrayList<>();
        rv.setAdapter(new StudentLeaveRecyclerViewAdapter(leaveList, this, this));
        rv.clearOnScrollListeners();
        rv.addOnScrollListener(new AbstractLoadMoreListener() {
            @Override
            protected void onLoading(int countItem, int lastItem) {
                PageUtils.getNextPageAndSize(leavePage, t -> initRecycleViewData(false, t.getT1(), t.getT2()));
            }
        });
        initRecycleViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE);
    }

    /**
     * 初始化数据
     *
     * @param clear 清空原有？
     * @param page  页
     * @param size  数量
     */
    private void initRecycleViewData(boolean clear, @Nullable Integer page, @Nullable Integer size) {
        swipeRefreshLayout.setRefreshing(true);
        disposable = HttpHelper
                .get(LeaveClient.class)
                .getStudentLeaves(page, size)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pageRestModel -> {
                    if (pageRestModel.getData().getContent() == null) {
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

    /**
     * 初始化下拉刷新
     */
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent, R.color.class_color_1,
                R.color.class_color_2, R.color.class_color_3, R.color.class_color_4,
                R.color.class_color_5, R.color.class_color_6, R.color.class_color_7
        );
        swipeRefreshLayout.setOnRefreshListener(() -> initRecycleViewData(true, PageUtils.DEFAULT_PAGE, PageUtils.DEFAULT_SIZE));
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
    public void onBackPressed() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (newReasonDisposable != null && !newReasonDisposable.isDisposed()) {
            newReasonDisposable.dispose();
        }
        if (newLeaveDisposable != null && !newLeaveDisposable.isDisposed()) {
            newLeaveDisposable.dispose();
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

    /**
     * 新增请假信息
     *
     * @param view View
     */
    public void onFabClick(View view) {
        leave = new Leave();
        newLeaveDialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View newLeaveReasonView = getLayoutInflater().inflate(R.layout.alert_leave_new, null);
        AppCompatSpinner leaveTypeSpinner = newLeaveReasonView.findViewById(R.id.spinner_type);
        startTextView = newLeaveReasonView.findViewById(R.id.tv_start_time);
        endTextView = newLeaveReasonView.findViewById(R.id.tv_end_time);
        newLeaveReasonViewTextInputLayout = newLeaveReasonView.findViewById(R.id.ti_layout);
        String nowDateStr = LocalDateTime.now(ZONE_ID).format(YYYYMMDD_DATE_TIME_FORMATTER_7);
        assert startTextView != null;
        assert endTextView != null;
        assert newLeaveReasonViewTextInputLayout != null;
        startTextView.setText(nowDateStr);
        endTextView.setText(nowDateStr);

        startTextView.setOnClickListener(this);
        endTextView.setOnClickListener(this);

        leaveTypeSpinner.setOnItemSelectedListener(this);

        EditText editText = newLeaveReasonViewTextInputLayout.getEditText();
        if (editText != null) {
            editText.setSingleLine();
            editText.setOnEditorActionListener(this);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    newLeaveReasonViewTextInputLayout.setError(null);
                }
            });
        }
        newLeaveDialog.setContentView(newLeaveReasonView);
        newLeaveDialog.show();
    }

    @Override
    public void onItemClick(View view, Leave leave) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View leaveReasonView = getLayoutInflater().inflate(R.layout.alert_leave_reason, null);
        RecyclerView recyclerView = leaveReasonView.findViewById(R.id.recycler_view);
        TextInputLayout textInputLayout = leaveReasonView.findViewById(R.id.ti_layout);
        TextView reasonTextView = leaveReasonView.findViewById(R.id.tv_reason);
        String reason = "原因：" + leave.getReason();
        reasonTextView.setText(reason);
        initReason(recyclerView, textInputLayout, leave);
        dialog.setContentView(leaveReasonView);
        dialog.show();
    }

    /**
     * 初始化请假评论
     *
     * @param recyclerView    RecyclerView
     * @param textInputLayout TextInputLayout
     * @param leave           请假信息
     */
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

    /**
     * 处理新增评论
     *
     * @param adapter         适配器
     * @param textInputLayout TextInputLayout
     * @param editText        EditText
     * @param leave           评论所在请假信息
     */
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

    @Override
    public void onClick(View v) {
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, Calendar.getInstance());
        switch (v.getId()) {
            case R.id.tv_start_time: {
                datePickerDialog.setMinDate(Calendar.getInstance());
                if (leave != null && leave.getEndTime() != null) {
                    Calendar maxCal = Calendar.getInstance();
                    maxCal.setTime(leave.getEndTime());
                    datePickerDialog.setMaxDate(maxCal);
                }
                datePickerDialog.show(getSupportFragmentManager(), "startDatePickerDialog");
                break;
            }
            case R.id.tv_end_time: {
                if (leave != null && leave.getStartTime() != null) {
                    Calendar minCal = Calendar.getInstance();
                    minCal.setTime(leave.getStartTime());
                    datePickerDialog.setMinDate(minCal);
                } else {
                    datePickerDialog.setMinDate(Calendar.getInstance());
                }
                datePickerDialog.show(getSupportFragmentManager(), "endDatePickerDialog");
                break;
            }
            default:
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (view.getTag() == null || leave == null) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String nowDateStr = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        switch (view.getTag()) {
            case "startDatePickerDialog": {
                leave.setStartTime(calendar.getTime());
                if (startTextView != null) {
                    startTextView.setText(nowDateStr);
                }
                break;
            }
            case "endDatePickerDialog": {
                leave.setEndTime(calendar.getTime());
                if (endTextView != null) {
                    endTextView.setText(nowDateStr);
                }
                break;
            }
            default:
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                if (leave != null) {
                    leave.setLeaveType(LeaveType.CLASS_LEAVE);
                }
                break;
            }
            case 1: {
                if (leave != null) {
                    leave.setLeaveType(LeaveType.ROOM_LEAVE);
                }
                break;
            }
            case 2: {
                if (leave != null) {
                    leave.setLeaveType(LeaveType.ALL_LEAVE);
                }
                break;
            }
            default:
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
            if (leave == null || newLeaveReasonViewTextInputLayout == null) {
                Toast.makeText(this, "请重新输入", Toast.LENGTH_LONG).show();
                return false;
            }
            Calendar calendar = Calendar.getInstance();
            if (leave.getStartTime() == null) {
                leave.setStartTime(calendar.getTime());
            }
            if (leave.getEndTime() == null) {
                calendar.setTime(leave.getStartTime());
                leave.setEndTime(calendar.getTime());
            }
            EditText editText = newLeaveReasonViewTextInputLayout.getEditText();
            if (editText != null) {
                if (editText.getText().length() == 0 || "".contentEquals(editText.getText())) {
                    newLeaveReasonViewTextInputLayout.setError("请输入请假原因");
                    return false;
                }
                handleNewLeave(leave, newLeaveReasonViewTextInputLayout, editText);
                return true;
            }
        }
        return false;
    }

    /**
     * 处理新增请假信息
     *
     * @param leave           请假信息
     * @param textInputLayout TextInputLayout
     * @param editText        EditText
     */
    @SuppressWarnings("deprecation")
    private void handleNewLeave(@NonNull Leave leave, @NonNull TextInputLayout textInputLayout, @NonNull EditText editText) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在发送");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        newLeaveDisposable = HttpHelper.get(LeaveClient.class)
                .newLeave(DateUtils.format(leave.getStartTime(), YYYYMMDD_DATE_TIME_FORMATTER_7),
                        DateUtils.format(leave.getEndTime(), YYYYMMDD_DATE_TIME_FORMATTER_7),
                        editText.getText().toString(),
                        leave.getLeaveType().name())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restModel -> {
                    progressDialog.dismiss();
                    textInputLayout.clearFocus();
                    editText.clearFocus();
                    editText.setText("");
                    leaveList.add(0, restModel.getData());
                    if (newLeaveDialog != null) {
                        newLeaveDialog.dismiss();
                    }
                    if (rv.getAdapter() != null) {
                        rv.getAdapter().notifyDataSetChanged();
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
