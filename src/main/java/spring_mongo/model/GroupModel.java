package spring_mongo.model;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

/**
 * Created by guxiaowei on 2017/7/6.
 */
public class GroupModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private ObjectId _id;//
    private String type;//类型
    private String name;//组标识级类型名称
    private String tel;//客服电话
    private String address;//地址
    private String city;//城市
    private String response_time;//响应时效
    private  Object point;//位置
    private  ArrayList place;//服务站
    private ArrayList fix_type;//二级维修类型
    private  ArrayList fix_type_group;//一级维修类型
    private  Boolean vip;//是否VIP
    private  String work_due_time;//完成时效
    private Date createAt;//创建时间
    private Date updateAt;//更新时间
    private  String createBy;//创建者ID
    private  String updateBy;//更新者ID
    private  String description;//描述
    private  String diff_time;//预警时间
    private String  parent;//父类id

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    private  String ObjectID;//
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    private String kind;//楼宇单元类型
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    public GroupModel() {
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

    public String getTel() {return tel;}

    public void setTel(String tel) {this.tel = tel;}

    public String getAddress() {return address;}

    public void setAddress(String address) {this.address = address;}

    public String getCity() {return city;}

    public void setCity(String city) {this.city = city;}

    public String getResponse_time() {return response_time;}

    public void setResponse_time(String response_time) {this.response_time = response_time;}

    public Boolean getVip() {return vip;}

    public void setVip(Boolean vip) {this.vip = vip;}

    public String getWork_due_time() {return work_due_time;}

    public void setWork_due_time(String work_due_time) {this.work_due_time = work_due_time;}

    public Date getCreateAt() {return createAt;}

    public void setCreateAt(Date createAt) {this.createAt = createAt;}

    public Date getUpdateAt() {return updateAt;}

    public void setUpdateAt(Date updateAt) {this.updateAt = updateAt;}

    public String getCreateBy() {return createBy;}

    public void setCreateBy(String createBy) {this.createBy = createBy;}

    public String getUpdateBy() {return updateBy;}

    public void setUpdateBy(String updateBy) {this.updateBy = updateBy;}

    public String getDescription() {return description;}

    public void setDescription(String description) {this.description = description;}

    public String getDiff_time() {return diff_time;}

    public void setDiff_time(String diff_time) {this.diff_time = diff_time;}

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}

    public Object getPoint() {return point;}

    public void setPoint(Object point) {this.point = point;}

    public ArrayList getPlace() {return place;}

    public void setPlace(ArrayList place) {this.place = place;}

    public ArrayList getFix_type() {return fix_type;}

    public void setFix_type(ArrayList fix_type) {this.fix_type = fix_type;}

    public ArrayList getFix_type_group() {return fix_type_group;}

    public void setFix_type_group(ArrayList fix_type_group) {this.fix_type_group = fix_type_group;}
}
