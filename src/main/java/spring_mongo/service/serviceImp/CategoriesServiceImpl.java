package spring_mongo.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.CategoriesDao;
import spring_mongo.model.CategoriesModel;
import spring_mongo.service.CategoriesService;

import java.util.List;

/**
 * Created by augadrian on 2017/7/19.
 */
@Service
@Transactional
public class CategoriesServiceImpl implements CategoriesService {
   @Autowired
   private CategoriesDao categoriesDao;
    @Override
    public List<CategoriesModel> getFixTtypeGroup(String engineerManager) {
        return categoriesDao.getFixTtypeGroup(engineerManager);
    }
    @Override
    public List<CategoriesModel> getFixTtype(String objectID) {
        return categoriesDao.getFixTtype(objectID);
    }
}
