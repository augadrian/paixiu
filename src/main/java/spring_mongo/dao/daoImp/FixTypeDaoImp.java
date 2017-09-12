package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import spring_mongo.dao.FixTypeDao;
import spring_mongo.model.FixTypeModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/19.
 */
@Component("FixTypeDaoImp")
public class FixTypeDaoImp extends AbstractBaseMongoTemplete implements FixTypeDao{
    @Override
    public List<FixTypeModel> getListByParent(String parent) {
        Criteria criteria = new Criteria("parent");
        criteria.is(parent);
        Query query = new Query(criteria);
        List<FixTypeModel> fixTypeModels = mongoTemplate.find(query,FixTypeModel.class,"Default.categories");
        return fixTypeModels.size()>0?fixTypeModels:null;
    }
}
