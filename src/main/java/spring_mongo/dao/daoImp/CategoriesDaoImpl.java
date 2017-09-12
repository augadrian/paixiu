package spring_mongo.dao.daoImp;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import spring_mongo.dao.CategoriesDao;
import spring_mongo.model.CategoriesModel;
import spring_mongo.model.EngineerModel;
import spring_mongo.dao.TicketDao;

import java.util.List;

/**
 * Created by augadrian on 2017/7/19.
 */
@Component("CategoriesDaoImpl")
public class CategoriesDaoImpl  extends AbstractBaseMongoTemplete implements CategoriesDao {

    @Autowired
    private TicketDao ticketDao;

    @Override
    public List<CategoriesModel>  getFixTtypeGroup(String engineerManager) {

        EngineerModel engineerModel=ticketDao.getEngineerByID(engineerManager);
        Query query = new Query();
        Criteria criteria = new Criteria("_id");
        criteria.in(engineerModel.getFix_type_group());
        query.addCriteria(criteria);
       List<CategoriesModel>  categoriesModelList = mongoTemplate.find(query, CategoriesModel.class, "Default.categories");
        return categoriesModelList;
    }
    @Override
    public List<CategoriesModel>  getFixTtype(String objectID) {
       Query query=new Query();
        Criteria criteria1 = new Criteria("parent");
        criteria1.is(objectID);
         query.addCriteria(criteria1);
        List<CategoriesModel>  categoriesModelList = mongoTemplate.find(query, CategoriesModel.class, "Default.categories");
        return categoriesModelList;
    }
}
