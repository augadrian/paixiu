package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Created by guxiaowei on 2017/7/21.
 */
public class MaterielModel implements Serializable {
    private static final long serialVersionUID = 1L;
    private ObjectId _id;
    private String objectID;
    private String name;
    private String type;
    private String parent;

    public MaterielModel() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
