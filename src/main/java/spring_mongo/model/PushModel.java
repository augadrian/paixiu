package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by guxiaowei on 2017/7/5.
 */
public class PushModel implements Serializable {
    private static final long serialVersionUID = 1L;
    //    private ObjectId _id;//
//    private String objectID;
    private String user; //用户
//    private Object pushList; //消息设置
    private String warn;
    private  String overtime;
    private  String order;
    private  String  examine ;
    private  String  bad;


    @Override
    public String toString() {
        return "PushModel{" +
                "user='" + user + '\'' +
                ", warn='" + warn + '\'' +
                ", overtime='" + overtime + '\'' +
                ", order='" + order + '\'' +
                ", examine='" + examine + '\'' +
                ", bad='" + bad + '\'' +
                '}';
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

//    public ObjectId get_id() {
//        return _id;
//    }
//
//    public void set_id(ObjectId _id) {
//        this._id = _id;
//    }
//
//    public String getObjectID() {
//        return objectID;
//    }
//
//    public void setObjectID(String objectID) {
//        this.objectID = objectID;
//    }


    public PushModel(String user, String warn, String overtime, String order, String examine, String bad) {
        this.user = user;
        this.warn = warn;
        this.overtime = overtime;
        this.order = order;
        this.examine = examine;
        this.bad = bad;
    }

    public PushModel() {

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getExamine() {
        return examine;
    }

    public void setExamine(String examine) {
        this.examine = examine;
    }

    public String getBad() {
        return bad;
    }

    public void setBad(String bad) {
        this.bad = bad;
    }
}
