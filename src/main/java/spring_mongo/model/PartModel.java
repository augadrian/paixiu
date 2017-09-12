package spring_mongo.model;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Created by guxiaowei on 2017/7/21.
 */
public class PartModel implements Serializable {
    private String name;
    private Integer count;

    public PartModel(String name, Integer count) {
        this.name = name;
        this.count = count;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
