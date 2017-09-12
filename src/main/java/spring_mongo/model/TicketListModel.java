package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

/**
 * Created by guxiaowei on 2017/7/5.
 */

/**
 * 抢单列表基础类
 */
public class TicketListModel implements Serializable{
    private static final long serialVersionUID = 1L;
    private ObjectId _id;//
    private String objectID;
    private String customer;//联系人
    private String no;//工单号
    private String createTime;//派单时间
    private String content;//故障描述
    private Date createAt;
    private String status;//工单状态
    private String level;//危机程度
    private Integer grade;//工单等级
    private Boolean is_vip;//VIP标识
    private String from;//工单来源
    private Integer grabTime;//自定义抢单时长
    private String grabOverTime;//抢单结束时间
    private String fix_type;//维修类型
    private String address;//地址
    private String project;//项目
    private String area;//区域
    private String unit;//地址
    private String room;//详细地址
    private List<String> history;//工单操作记录
    private String main_engineer;//工程师ID
    private String engineerName;//工程师姓名
    private int verificate_time;//核定工时
    private int standard_time;//标准工时
    private int service_time;//服务时长
    private String checkState;  //审核状态 筛选时 插入  多条件 ， 拼接  0--待审核  1--审核通过 2--审核未通过
    private Date accept_time;//接单时间
    private Date finish_time;//结单时间
    private Date assign_time;//派单时间

    public TicketListModel() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }

    public String getGrabOverTime() {
        return grabOverTime;
    }

    public void setGrabOverTime(String grabOverTime) {
        this.grabOverTime = grabOverTime;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Boolean getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(Boolean is_vip) {
        this.is_vip = is_vip;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Integer getGrabTime() {
        return grabTime;
    }

    public void setGrabTime(Integer grabTime) {
        this.grabTime = grabTime;
    }

    public String getFix_type() {
        return fix_type;
    }

    public void setFix_type(String fix_type) {
        this.fix_type = fix_type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
    public String getEngineerName() {
        return engineerName;
    }

    public void setEngineerName(String engineerName) {
        this.engineerName = engineerName;
    }

    public int getVerificate_time() {
        return verificate_time;
    }

    public void setVerificate_time(int verificate_time) {
        this.verificate_time = verificate_time;
    }

    public int getStandard_time() {
        return standard_time;
    }

    public void setStandard_time(int standard_time) {
        this.standard_time = standard_time;
    }

    public int getService_time() {
        return service_time;
    }

    public void setService_time(int service_time) {
        this.service_time = service_time;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }
    public String getMain_engineer() {
        return main_engineer;
    }

    public void setMain_engineer(String main_engineer) {
        this.main_engineer = main_engineer;
    }

    public Date getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(Date accept_time) {
        this.accept_time = accept_time;
    }

    public Date getFinish_time() {
        return finish_time;
    }

    public void setFinish_time(Date finish_time) {
        this.finish_time = finish_time;
    }

    public Date getAssign_time() {
        return assign_time;
    }

    public void setAssign_time(Date assign_time) {
        this.assign_time = assign_time;
    }
}
