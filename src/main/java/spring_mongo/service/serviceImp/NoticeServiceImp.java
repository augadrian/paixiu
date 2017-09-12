package spring_mongo.service.serviceImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.NoticeDao;
import spring_mongo.dao.TicketDao;
import spring_mongo.model.EngineerModel;
import spring_mongo.model.NoticeModel;
import spring_mongo.model.TicketModel;
import spring_mongo.service.NoticeService;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */
@Service
@Transactional
public class NoticeServiceImp implements NoticeService {
    @Autowired
    private NoticeDao noticeDao;
    @Autowired
    private TicketDao ticketDao;


    @Override
    public List<NoticeModel> findNoticeByUserId(String userId) {
        return noticeDao.findNoticeByUserId(userId);
    }

    @Override
    public List<NoticeModel> findNoticeByUserIdNo(String userId) {
        return noticeDao.findNoticeByUserIdNo(userId);
    }

    @Override
    public List<NoticeModel> findNoticeByUserId(String userId, Integer pageNo, Integer pageSize) {
        List<NoticeModel> list = noticeDao.findNoticeByUserId(userId, pageNo, pageSize);
        Iterator<NoticeModel> iterator = list.iterator();
        while (iterator.hasNext()) {
            NoticeModel noticeModel = iterator.next();
            TicketModel ticketModel = ticketDao.getByID(noticeModel.getTicketId());
//            System.out.println(ticketModel.get_id());
//            EngineerModel engineerModel = ticketDao.getEngineerByID(ticketModel.getMain_engineer());
//            System.out.println(engineerModel.get_id());
            noticeModel.setIs_vip(ticketModel.getIs_vip() ? "1" : "0");
            noticeModel.setCustomer(ticketModel.getCustomer());
        }
        return list;
    }

    @Override
    public void insert(NoticeModel noticeModel) {
        noticeDao.insert(noticeModel);
    }

    @Override
    public void updateState(String noticeId, String state) {
        noticeDao.updateState(noticeId, state);
    }
}
