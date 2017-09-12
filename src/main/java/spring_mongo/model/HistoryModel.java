package spring_mongo.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by guxiaowei on 2017/7/13.
 */
public class HistoryModel implements Serializable {
    private String action;
    private String status;
    private String user;
    private Date date;
    private  String staff;

    public HistoryModel(String action, String status, String user, Date date) {
        this.action = action;
        this.status = status;
        this.user = user;
        this.date = date;
    }

    public HistoryModel() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }
}
