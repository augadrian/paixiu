package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import spring_mongo.dao.SysUserDao;
import spring_mongo.model.SysUserModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/8/30.
 */
@Component("SysUserDaoImp")
public class SysUserDaoImp extends AbstractBaseMongoTemplete implements SysUserDao {
    @Override
    public SysUserModel getByID(String id) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        Query query = new BasicQuery(dbObject);
        List<SysUserModel> sysUserModels = mongoTemplate.find(query, SysUserModel.class, "Default.users");
        return sysUserModels != null ? sysUserModels.get(0) : null;
    }
}
