package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import spring_mongo.dao.PushDao;
import spring_mongo.model.PushModel;
import spring_mongo.model.TicketModel;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
@Component("PushDaoImp")
public class PushDaoImp extends AbstractBaseMongoTemplete implements PushDao {
    /**
     * 通过用户id  找到推送设置记录
     *
     * @param userId
     * @return
     */
    @Override
    public List<PushModel> findPushByUserId(String userId) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("user", userId);
        Query query = new BasicQuery(dbObject);
        System.out.println(userId);
        List<PushModel> pushModelList = mongoTemplate.find(query, PushModel.class, "push_user");
        return pushModelList;
    }

    /**
     * 新增记录
     *
     * @param pushModel
     */
    @Override
    public void insertPush(PushModel pushModel) {
        DBObject object = new BasicDBObject();
        object.put("bad", pushModel.getBad());
        object.put("examine", pushModel.getExamine());
        object.put("order", pushModel.getOrder());
        object.put("overtime", pushModel.getOvertime());
        object.put("user", pushModel.getUser());
        object.put("warn", pushModel.getWarn());
        mongoTemplate.insert(object, "push_user");
    }


    @Override
    public PushModel updatePush(String userId, String name, String value) {
        Query query = new Query();
        Criteria criteria = new Criteria("user");
        criteria.is(userId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set(name, value);
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "push_user");
        return getByUserId(userId) != null ? getByUserId(userId) : null;
    }

    @Override
    public PushModel getByUserId(String userId) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("user", userId);
        Query query = new BasicQuery(dbObject);
        List<PushModel> pushModelList = mongoTemplate.find(query, PushModel.class, "push_user");
        return pushModelList != null ? pushModelList.get(0) : null;
    }


}
