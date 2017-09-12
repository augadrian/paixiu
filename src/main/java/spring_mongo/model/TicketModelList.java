package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

/**
 * Created by augadrian on 2017/7/19.
 */
public class TicketModelList implements Serializable {
    private static final long serialVersionUID = 1L;

    private ObjectId _id;//
    private String objectID;
    private String phone;//客户电话
    private String customer;//客户名
    private List<Model> project;//项目
    private List<Model> area;//区域
    private List<Model> fix_type;//二级维修类型
    private String selectList;  //      0--已挂起   1--已完成
    private List<Model> fix_type_group;//一级维修类型
    private List<Model> unit;//楼宇单元
    private  String name;//工程师name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public List<Model> getProject() {
        return project;
    }

    public void setProject(List<Model> project) {
        this.project = project;
    }

    public List<Model> getArea() {
        return area;
    }

    public void setArea(List<Model> area) {
        this.area = area;
    }

    public List<Model> getFix_type() {
        return fix_type;
    }

    public void setFix_type(List<Model> fix_type) {
        this.fix_type = fix_type;
    }

    public List<Model> getFix_type_group() {
        return fix_type_group;
    }

    public void setFix_type_group(List<Model> fix_type_group) {
        this.fix_type_group = fix_type_group;
    }

    public List<Model> getUnit() {
        return unit;
    }

    public void setUnit(List<Model> unit) {
        this.unit = unit;
    }

    public String getSelectList() {
        return selectList;
    }

    public void setSelectList(String selectList) {
        this.selectList = selectList;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
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

}
