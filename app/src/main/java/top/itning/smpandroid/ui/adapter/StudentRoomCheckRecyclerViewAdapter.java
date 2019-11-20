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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import top.itning.smpandroid.R;
import top.itning.smpandroid.entity.RoomCheck;
import top.itning.smpandroid.ui.view.RoundBackChange;

/**
 * @author itning
 */
public class StudentRoomCheckRecyclerViewAdapter extends RecyclerView.Adapter<StudentRoomCheckRecyclerViewAdapter.ViewHolder> {
    private static final ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT_THREAD_LOCAL = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA));
    private final List<RoomCheck> roomCheckList;
    private Context context;
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;


    public StudentRoomCheckRecyclerViewAdapter(@NonNull List<RoomCheck> roomCheckList, @NonNull Context context) {
        this.roomCheckList = roomCheckList;
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
        RoomCheck roomCheck = roomCheckList.get(position);
        holder.date.setText(Objects.requireNonNull(SIMPLE_DATE_FORMAT_THREAD_LOCAL.get()).format(roomCheck.getCheckDate()));
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return roomCheckList.size();
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
