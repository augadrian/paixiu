package spring_mongo.model;

import java.io.Serializable;
import java.util.Date;

public class UserModel implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int age;
	private String sex;
	private Date createAt;


	public UserModel() {
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "UserModel [name=" + name + ", age=" + age + "]";
	}

}
