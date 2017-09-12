package spring_mongo.service;

import spring_mongo.model.FixTypeModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/19.
 */
public interface FixTypeService {
    public List<FixTypeModel> getListByParent(String parent);
}
