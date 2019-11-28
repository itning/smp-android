package top.itning.smpandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import top.itning.smpandroid.R;
import top.itning.smpandroid.entity.LeaveReason;
import top.itning.smpandroid.ui.view.RoundBackChange;
import top.itning.smpandroid.util.DateUtils;

/**
 * @author itning
 */
public class StudentLeaveReasonRecyclerViewAdapter extends RecyclerView.Adapter<StudentLeaveReasonRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private final List<LeaveReason> leaveReasonList;
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;

    public StudentLeaveReasonRecyclerViewAdapter(@NonNull List<LeaveReason> leaveReasonList, @NonNull Context context) {
        this.context = context;
        this.leaveReasonList = leaveReasonList;
        initColorArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_leave_reason, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveReason leaveReason = leaveReasonList.get(position);
        holder.from.setText(getFromStr(leaveReason));
        holder.content.setText(leaveReason.getComment());
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return leaveReasonList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView from;
        private TextView content;
        private RoundBackChange roundBackChange;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            set(itemView);
        }

        private void set(View itemView) {
            this.from = itemView.findViewById(R.id.tv_from);
            this.content = itemView.findViewById(R.id.tv_content);
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

    private String getFromStr(LeaveReason leaveReason) {
        return leaveReason.getFromUser().getName() + " " + DateUtils.format(leaveReason.getGmtCreate(), DateUtils.YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_8);
    }
}
