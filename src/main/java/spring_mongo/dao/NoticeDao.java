package spring_mongo.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.model.NoticeModel;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */
@Transactional
public interface NoticeDao {
    //插入通知消息
    public List<NoticeModel> findNoticeByUserId(String userId);
    public List<NoticeModel> findNoticeByUserIdNo(String userId);
    public List<NoticeModel> findNoticeByUserId(String userId, Integer pageNo, Integer  pageSize);
    public void insert(NoticeModel noticeModel);
    public void updateState(String noticeId,String state);
}
