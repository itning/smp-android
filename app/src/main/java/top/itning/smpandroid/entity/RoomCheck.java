package top.itning.smpandroid.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
public class RoomCheck implements Serializable {
    private Date checkDate;

    public RoomCheck() {
    }

    public RoomCheck(Date checkData) {
        this.checkDate = checkData;
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }
}
