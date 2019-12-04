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
import top.itning.smpandroid.entity.StudentClassCheckDTO;
import top.itning.smpandroid.ui.view.RoundBackChange;
import top.itning.smpandroid.util.DateUtils;

/**
 * @author itning
 */
public class StudentCheckDetailRecyclerViewDataAdapter extends RecyclerView.Adapter<StudentCheckDetailRecyclerViewDataAdapter.ViewHolder> {
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;

    private final Context context;
    private final List<StudentClassCheckDTO> studentClassCheckDtoList;

    public StudentCheckDetailRecyclerViewDataAdapter(Context context, List<StudentClassCheckDTO> studentClassCheckDtoList) {
        this.context = context;
        this.studentClassCheckDtoList = studentClassCheckDtoList;
        initColorArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_class_user_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentClassCheckDTO studentClassCheckDto = studentClassCheckDtoList.get(position);
        holder.checkTime.setText(getCheckTime(studentClassCheckDto));
        holder.checkStatus.setText(getCheckStatus(studentClassCheckDto));
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return studentClassCheckDtoList.size();
    }

    private String getCheckTime(StudentClassCheckDTO studentClassCheckDto) {
        if (studentClassCheckDto.getCheckTime() == null) {
            return DateUtils.format(studentClassCheckDto.getGmtCreate(), DateUtils.YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_1);
        } else {
            return DateUtils.format(studentClassCheckDto.getGmtCreate(), DateUtils.YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_1);
        }
    }

    private String getCheckStatus(StudentClassCheckDTO studentClassCheckDto) {
        if (studentClassCheckDto.getCheck() == null) {
            return "请假";
        } else if (studentClassCheckDto.getCheck()) {
            return "已签到";
        } else {
            return "未签到";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView checkTime;
        private TextView checkStatus;
        private RoundBackChange roundBackChange;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            set(itemView);
        }

        private void set(View itemView) {
            this.checkTime = itemView.findViewById(R.id.tv_check_time);
            this.checkStatus = itemView.findViewById(R.id.tv_check_status);
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
}
