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
import top.itning.smpandroid.entity.StudentRoomCheck;
import top.itning.smpandroid.ui.view.RoundBackChange;
import top.itning.smpandroid.util.DateUtils;

/**
 * @author itning
 */
public class StudentRoomCheckRecyclerViewAdapter extends RecyclerView.Adapter<StudentRoomCheckRecyclerViewAdapter.ViewHolder> {
    private final List<StudentRoomCheck> studentRoomCheckList;
    private final Context context;
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;


    public StudentRoomCheckRecyclerViewAdapter(@NonNull List<StudentRoomCheck> studentRoomCheckList, @NonNull Context context) {
        this.studentRoomCheckList = studentRoomCheckList;
        this.context = context;
        initColorArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_check, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentRoomCheck studentRoomCheck = studentRoomCheckList.get(position);
        holder.date.setText(DateUtils.format(studentRoomCheck.getCheckTime(), DateUtils.YYYYMMDDHHMMSS_DATE_TIME_FORMATTER_1));
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return studentRoomCheckList.size();
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
