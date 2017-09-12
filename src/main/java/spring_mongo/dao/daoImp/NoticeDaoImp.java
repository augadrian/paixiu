package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import spring_mongo.dao.NoticeDao;
import spring_mongo.model.NoticeModel;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */
@Component("NoticeDaoImp")
public class NoticeDaoImp extends AbstractBaseMongoTemplete implements NoticeDao {

    /**
     * 通过用户id 获取消息列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<NoticeModel> findNoticeByUserId(String userId) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("userId", userId);
        Query query = new BasicQuery(dbObject);
        List<NoticeModel> noticeModelList = mongoTemplate.find(query, NoticeModel.class, "notice_user");
        return noticeModelList;
    }  /**
     * 通过用户id 获取未读消息列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<NoticeModel> findNoticeByUserIdNo(String userId) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("userId", userId);
        dbObject.put("state", "0"); //未读
        Query query = new BasicQuery(dbObject);
        List<NoticeModel> noticeModelList = mongoTemplate.find(query, NoticeModel.class, "notice_user");
        return noticeModelList;
    }

    @Override
    public List<NoticeModel> findNoticeByUserId(String userId, Integer pageNo, Integer pageSize) {
//        DBObject dbObject = new BasicDBObject();
//        dbObject.put("userId", userId);
        Sort sort = new Sort(Sort.Direction.ASC, "state");
        Query query = new Query();
        Criteria criteria = new Criteria("userId");
        criteria.in(userId);
        query.addCriteria(criteria);
        List<NoticeModel> noticeModelList = mongoTemplate.find(query, NoticeModel.class, "notice_user");
        return noticeModelList;
    }

    /**
     * 插入 消息
     *
     * @param noticeModel
     */
    @Override
    public void insert(NoticeModel noticeModel) {
        DBObject object = new BasicDBObject();
        object.put("userId", noticeModel.getUserId());
        object.put("ico", noticeModel.getIco());
        object.put("type", noticeModel.getType());
        object.put("ticketId", noticeModel.getTicketId());
        object.put("ticketNo", noticeModel.getTicketNo());
        object.put("address", noticeModel.getAddress());
        object.put("state", noticeModel.getState());
        object.put("engineerId", noticeModel.getEngineerId());
        object.put("engineerName", noticeModel.getEngineerName());
        mongoTemplate.insert(object, "notice_user");

    }

    /**
     * 更新已读 未读 的状态
     *
     * @param noticeId
     * @param state
     */
    @Override
    public void updateState(String noticeId, String state) {
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.is(noticeId);
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = new Update();
        update.set("state", state);
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "notice_user");

    }
}
