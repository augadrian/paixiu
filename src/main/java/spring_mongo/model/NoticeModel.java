package spring_mongo.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/13.
 */
public class NoticeModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id; //id
    private String userId;  //用户
    private String customer;//客户名
    private String ico; //图标
    private String type;//通知类型
    private String ticketId;//工单id
    private String is_vip;//是否vip工单
    private String ticketNo;//单号
    private String address; //地址
    private String engineerId;  //工程师id
    private String engineerName;  //工程师姓名
    private String state;  //状态   0 --未读   1--已读

    public NoticeModel() {
    }

    public NoticeModel(String id, String userId, String ico, String type, String ticketNo, String address, String engineerId, String engineerName, String state) {
        this.id = id;
        this.userId = userId;
        this.ico = ico;
        this.type = type;
        this.ticketNo = ticketNo;
        this.address = address;
        this.engineerId = engineerId;
        this.engineerName = engineerName;
        this.state = state;
    }

    @Override
    public String toString() {
        return "NoticeModel{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", ico='" + ico + '\'' +
                ", ticketType='" + type + '\'' +
                ", ticketNo='" + ticketNo + '\'' +
                ", address='" + address + '\'' +
                ", engineerId='" + engineerId + '\'' +
                ", engineerName='" + engineerName + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }


    public String getEngineerId() {
        return engineerId;
    }

    public void setEngineerId(String engineerId) {
        this.engineerId = engineerId;
    }

    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
