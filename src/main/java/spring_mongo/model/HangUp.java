package spring_mongo.model;



/**
 * Created by augadrian on 2017/7/29.
 */
public class HangUp {

    private String reason;//挂单理由
    private String date;//挂单时间
    private String restart_time;//预约时间

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRestart_time() {
        return restart_time;
    }

    public void setRestart_time(String restart_time) {
        this.restart_time = restart_time;
    }






}
