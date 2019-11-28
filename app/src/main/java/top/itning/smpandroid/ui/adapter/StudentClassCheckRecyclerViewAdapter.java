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
import top.itning.smpandroid.entity.StudentClassCheck;
import top.itning.smpandroid.ui.view.RoundBackChange;
import top.itning.smpandroid.util.DateUtils;

/**
 * @author itning
 */
public class StudentClassCheckRecyclerViewAdapter extends RecyclerView.Adapter<StudentClassCheckRecyclerViewAdapter.ViewHolder> {
    private final List<StudentClassCheck> studentClassCheckList;
    private Context context;
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;


    public StudentClassCheckRecyclerViewAdapter(@NonNull List<StudentClassCheck> studentClassCheckList, @NonNull Context context) {
        this.studentClassCheckList = studentClassCheckList;
        this.context = context;
        initColorArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_class_check, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentClassCheck studentClassCheck = studentClassCheckList.get(position);
        holder.date.setText(DateUtils.format(studentClassCheck.getCheckTime(), DateUtils.YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_1));
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return studentClassCheckList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private RoundBackChange roundBackChange;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            set(itemView);
        }

        private void set(View itemView) {
            this.date = itemView.findViewById(R.id.tv_date);
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
