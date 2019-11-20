package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
public class StudentGroupCheck implements Serializable {
    private Date checkDate;

    public StudentGroupCheck() {
    }

    public StudentGroupCheck(Date checkDate) {
        this.checkDate = checkDate;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }
}
