package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import spring_mongo.dao.BoardsDao;
import spring_mongo.model.BoardsModel;

import java.util.List;

/**
 * Created by guxiaowei on 2017/7/3.
 */
@Component("BoardsDaoImp")
public class BoardsDaoImp extends AbstractBaseMongoTemplete implements BoardsDao{
    @Override
    public List<BoardsModel> findAll() {
        List<BoardsModel> boardsModelList = mongoTemplate.findAll(BoardsModel.class, "light.boards");
        return boardsModelList;
    }

    @Override
    public void insertUser(BoardsModel boardsModel) {
        // 设置需要插入到数据库的文档对象
        DBObject object = new BasicDBObject();
        object.put("action", boardsModel.getAction());
        object.put("api", boardsModel.getApi());
        mongoTemplate.insert(object, "light.boards");
    }

    @Override
    public void removeUser(String boardName) {
        // 设置删除条件，如果条件内容为空则删除所有
        Query query = new Query();
        Criteria criteria = new Criteria("action");
        criteria.is(boardName);
        query.addCriteria(criteria);
        mongoTemplate.remove(query, "light.boards");
    }

    @Override
    public void updateUser(BoardsModel boardsModel) {

        // 设置修改条件
        Query query = new Query();
        Criteria criteria = new Criteria("action");
        criteria.is(boardsModel.getAction());
        query.addCriteria(criteria);
        // 设置修改内容
        Update update = Update.update("api", boardsModel.getApi());
        // 参数：查询条件，更改结果，集合名
        mongoTemplate.updateFirst(query, update, "light.boards");
    }

    @Override
    public List<BoardsModel> findForRequery(String boardName) {
        Query query = new Query();
        Criteria criteria = new Criteria("action");
        criteria.is(boardName);
        query.addCriteria(criteria);
        List<BoardsModel> boardsModelList = mongoTemplate.find(query,BoardsModel.class,"light.boards");
        return boardsModelList;
    }

    @Override
    public BasicDBList mongoGroup() {
        return null;
    }

    @Override
    public void saveData(DBObject obj) {
        mongoTemplate.save(obj, "light.boards");
    }
}
