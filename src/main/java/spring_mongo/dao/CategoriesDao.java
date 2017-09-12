package spring_mongo.dao;

import org.springframework.transaction.annotation.Transactional;
import spring_mongo.model.CategoriesModel;

import java.util.List;

/**
 * Created by augadrian on 2017/7/19.
 */
@Transactional
public interface CategoriesDao {

    /**
     *
     * 获取一级维修类型
     * @return
     */
    List<CategoriesModel> getFixTtypeGroup(String engineerManager);

    List<CategoriesModel> getFixTtype(String objectID);
}
