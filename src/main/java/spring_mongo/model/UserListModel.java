package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.List;

public class UserListModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private ObjectId _id;//
	private String objectID;
	private String name; //姓名
	private List fix_type_group;  //维修类型
	private List fix_type;  //维修类型详情


	public List getFix_type_group() {
		return fix_type_group;
	}

	public void setFix_type_group(List fix_type_group) {
		this.fix_type_group = fix_type_group;
	}

	public List getFix_type() {
		return fix_type;
	}

	public void setFix_type(List fix_type) {
		this.fix_type = fix_type;
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


}
