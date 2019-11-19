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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import top.itning.smpandroid.R;
import top.itning.smpandroid.RoundBackChange;
import top.itning.smpandroid.entity.Group;

/**
 * @author itning
 */
public class StudentGroupRecyclerViewAdapter extends RecyclerView.Adapter<StudentGroupRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {
    private final Context context;
    private final OnItemClickListener<Group> onItemClickListener;
    private final List<Group> groupList;
    private final List<Integer> colorList = new ArrayList<>(7);
    private int nexIndex;

    public StudentGroupRecyclerViewAdapter(@NonNull List<Group> groupList, @NonNull Context context, @Nullable OnItemClickListener<Group> onItemClickListener) {
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.groupList = groupList;
        initColorArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_join_group_item, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.itemView.setTag(group);
        holder.peopleCount.setText(MessageFormat.format("{0}人", group.getCount()));
        holder.className.setText(group.getClassName());
        holder.teacherName.setText(group.getTeacherName());
        holder.roundBackChange.setBackColor(getNextColor());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Group) v.getTag());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView className;
        private TextView teacherName;
        private TextView peopleCount;
        private RoundBackChange roundBackChange;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            set(itemView);
        }

        private void set(View itemView) {
            this.className = itemView.findViewById(R.id.tv_class);
            this.teacherName = itemView.findViewById(R.id.tv_teacher);
            this.peopleCount = itemView.findViewById(R.id.tv_people);
            this.roundBackChange = itemView.findViewById(R.id.round);
        }
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
