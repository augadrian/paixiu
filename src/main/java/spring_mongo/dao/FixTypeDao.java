package spring_mongo.dao;

import org.springframework.transaction.annotation.Transactional;
import spring_mongo.model.FixTypeModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/19.
 */
@Transactional
public interface FixTypeDao {
    public List<FixTypeModel> getListByParent(String parent);
}
