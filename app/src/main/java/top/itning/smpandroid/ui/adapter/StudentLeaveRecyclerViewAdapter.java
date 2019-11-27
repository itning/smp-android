package top.itning.smpandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import top.itning.smpandroid.R;
import top.itning.smpandroid.entity.Leave;
import top.itning.smpandroid.ui.view.RoundBackChange;

/**
 * @author itning
 */
public class StudentLeaveRecyclerViewAdapter extends RecyclerView.Adapter<StudentLeaveRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA));
    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL_2 = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA));
    private final Context context;
    private final List<Leave> leaveList;
    private final OnItemClickListener<Leave> onItemClickListener;
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;

    public StudentLeaveRecyclerViewAdapter(@NonNull List<Leave> leaveList, @NonNull Context context, @Nullable OnItemClickListener<Leave> onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.leaveList = leaveList;
        initColorArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_leave, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Leave leave = leaveList.get(position);
        holder.itemView.setTag(leave);
        holder.type.setText(getTypeStr(leave));
        holder.status.setText(getStatusStr(leave));
        String timeStr = Objects.requireNonNull(SIMPLE_DATE_FORMAT_THREAD_LOCAL_2.get()).format(leave.getStartTime()) + "-" + Objects.requireNonNull(SIMPLE_DATE_FORMAT_THREAD_LOCAL_2.get()).format(leave.getEndTime());
        holder.time.setText(timeStr);
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return leaveList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView time;
        private TextView status;
        private RoundBackChange roundBackChange;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            set(itemView);
        }

        private void set(View itemView) {
            this.type = itemView.findViewById(R.id.tv_type);
            this.time = itemView.findViewById(R.id.tv_time);
            this.status = itemView.findViewById(R.id.tv_status);
            this.roundBackChange = itemView.findViewById(R.id.round);
        }
    }

    /**
     * 初始化颜色数组
     */
    private void initColorArray() {
        colorList.add(ContextCompat.getColor(context, R.color.class_color_1));
        colorList.add(ContextCompat.getColor(context, R.color.class_color_2));
        colorList.add(ContextCompat.getColor(context, R.color.class_color_3));
        colorList.add(ContextCompat.getColor(context, R.color.class_color_4));
        colorList.add(ContextCompat.getColor(context, R.color.class_color_5));
        colorList.add(ContextCompat.getColor(context, R.color.class_color_6));
        colorList.add(ContextCompat.getColor(context, R.color.class_color_7));
    }

    public interface OnItemClickListener<T> {
        /**
         * 当每一项点击时
         *
         * @param view   View
         * @param object 对象
         */
        void onItemClick(View view, T object);
    }

    /**
     * 获取随机颜色
     *
     * @return 颜色
     */
    @ColorInt
    private int getNextColor() {
        if (nexIndex == colorList.size()) {
            nexIndex = 0;
        }
        return colorList.get(nexIndex++);
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Leave) v.getTag());
        }
    }

    private String getTypeStr(@NonNull Leave leave) {
        StringBuilder sb = new StringBuilder();
        switch (leave.getLeaveType()) {
            case ALL_LEAVE: {
                sb.append("课假+寝室假");
                break;
            }
            case ROOM_LEAVE: {
                sb.append("寝室假");
                break;
            }
            case CLASS_LEAVE: {
                sb.append("课假");
                break;
            }
            default:
        }
        sb.append(" ").append(Objects.requireNonNull(SIMPLE_DATE_FORMAT_THREAD_LOCAL.get()).format(leave.getGmtCreate()));
        return sb.toString();
    }

    /**
     * 审核状态（true 通过；false 未通过；null 未审核）
     *
     * @param leave 请假信息
     * @return 审核状态
     */
    private String getStatusStr(@NonNull Leave leave) {
        if (leave.getStatus() == null) {
            return "未审核";
        } else {
            if (leave.getStatus()) {
                return "通过";
            } else {
                return "未通过";
            }
        }
    }
}
