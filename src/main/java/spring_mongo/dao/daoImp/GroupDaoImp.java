package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import spring_mongo.dao.GroupDao;
import spring_mongo.dao.TicketDao;
import spring_mongo.model.EngineerModel;
import spring_mongo.model.GroupModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/6.
 */
@Component("GroupDaoImp")
public class GroupDaoImp extends AbstractBaseMongoTemplete implements GroupDao {
    @Autowired
    private TicketDao ticketDao;

    @Override
    public GroupModel getByID(String id) {
        DBObject dbObject = new BasicDBObject();
        dbObject.put("_id", id);
        Query query = new BasicQuery(dbObject);
        List<GroupModel> groupModelList = mongoTemplate.find(query, GroupModel.class, "Default.groups");
        if (groupModelList.size() == 0) {
            return null;
        }
        return groupModelList != null ? groupModelList.get(0) : null;
    }

    @Override
    public List<GroupModel> getBuildings(String engineerManager) {
        EngineerModel engineerModel = ticketDao.getEngineerByID(engineerManager);
        Query query = new Query();
        Criteria criteria = new Criteria("area");
        criteria.in(engineerModel.getArea());
        query.addCriteria(criteria);
        Criteria criteria2 = new Criteria("kind");
        criteria2.is(0);
        query.addCriteria(criteria2);
        List<GroupModel> groupModelList = mongoTemplate.find(query, GroupModel.class, "Default.groups");
        return groupModelList.size() > 0 ? groupModelList : null;
    }

    @Override
    public List<GroupModel> getProjects(String engineerManager) {
        EngineerModel engineerModel = ticketDao.getEngineerByID(engineerManager);
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.in(engineerModel.getProject());
        query.addCriteria(criteria);
        Criteria criteria1 = new Criteria("type");
        criteria1.is("project");
        query.addCriteria(criteria1);

        List<GroupModel> groupModelList = mongoTemplate.find(query, GroupModel.class, "Default.groups");
        return groupModelList.size() > 0 ? groupModelList : null;

    }

    @Override
    public List<GroupModel> getAreas(String engineerManager) {
        EngineerModel engineerModel = ticketDao.getEngineerByID(engineerManager);
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.in(engineerModel.getArea());
        query.addCriteria(criteria);
        Criteria criteria1 = new Criteria("type");
        criteria1.is("area");
        query.addCriteria(criteria1);
        List<GroupModel> groupModelList = mongoTemplate.find(query, GroupModel.class, "Default.groups");
        return groupModelList.size() > 0 ? groupModelList : null;
    }

    @Override
    public List<GroupModel> getUnits(GroupModel groupModel) {
        Query query = new Query();
        Criteria criteria = new Criteria("parent");
        criteria.is(groupModel.getObjectID());
        query.addCriteria(criteria);
        List<GroupModel> groupModelList = mongoTemplate.find(query, GroupModel.class, "Default.groups");
        return groupModelList.size() > 0 ? groupModelList : null;

    }
}
