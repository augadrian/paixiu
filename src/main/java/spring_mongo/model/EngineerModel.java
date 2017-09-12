package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.sql.Array;
import java.util.List;

/**
 * Created by guxiaowei on 2017/7/8.
 */

/**
 * 工作人员基础类（工程师、工程管理者等）
 */
public class EngineerModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private ObjectId _id;
    private String objectID;
    private String name;
    private String state;
    private String work_status;
    private List<String> project;
    private List<String> unit;
    private List<String> area;
    private List<String> fix_type;//二级维修类型
    private String position;  //职位
    private Boolean is_vip;//是否vip工单
    private List<String> fix_type_group;//一级维修类型

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private  String phone;
    public EngineerModel() {
    }

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getWork_status() {
        return work_status;
    }

    public void setWork_status(String work_status) {
        this.work_status = work_status;
    }

    public List<String> getProject() {
        return project;
    }

    public void setProject(List<String> project) {
        this.project = project;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public List<String> getFix_type() {
        return fix_type;
    }

    public void setFix_type(List<String> fix_type) {
        this.fix_type = fix_type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(Boolean is_vip) {
        this.is_vip = is_vip;
    }

    public List<String> getFix_type_group() {
        return fix_type_group;
    }

    public void setFix_type_group(List<String> fix_type_group) {
        this.fix_type_group = fix_type_group;
    }

    public List<String> getUnit() {
        return unit;
    }

    public void setUnit(List<String> unit) {
        this.unit = unit;
    }
}
