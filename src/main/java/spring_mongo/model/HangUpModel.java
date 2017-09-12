package spring_mongo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by augadrian on 2017/7/25.
 */
public class HangUpModel implements Serializable {

    private String reason;//挂单理由
    private String date;//挂单时间
    private Date restart_time;//预约时间


    public Date getRestart_time() {
        return restart_time;
    }

    public void setRestart_time(Date restart_time) {
        this.restart_time = restart_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


}
