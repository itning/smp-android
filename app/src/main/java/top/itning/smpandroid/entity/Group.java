package top.itning.smpandroid.entity;

import java.io.Serializable;

/**
 * @author itning
 */
public class Group implements Serializable {
    private String className;
    private String teacherName;
    private int count;

    public Group() {
    }

    public Group(String className, String teacherName, int count) {
        this.teacherName = teacherName;
        this.className = className;
        this.count = count;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Group{" +
                "className='" + className + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", count=" + count +
                '}';
    }
}
