package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Created by guxiaowei on 2017/7/19.
 */
public class FixTypeModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private ObjectId _id;
    private String objectID;
    private String name;
    private Integer time;

    public FixTypeModel() {
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

    public String getObjectID() {
        return objectID;
    }

    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
