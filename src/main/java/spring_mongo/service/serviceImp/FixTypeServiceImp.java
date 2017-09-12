package spring_mongo.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.FixTypeDao;
import spring_mongo.model.FixTypeModel;
import spring_mongo.service.FixTypeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guxiaowei on 2017/7/19.
 */
@Service
@Transactional
public class FixTypeServiceImp implements FixTypeService {
    @Autowired
    private FixTypeDao fixTypeDao;
    @Override
    public List<FixTypeModel> getListByParent(String parent) {
        List<FixTypeModel> fixTypeModelList = fixTypeDao.getListByParent(parent);
        List<FixTypeModel> fixTypeModels = new ArrayList<FixTypeModel>();
        for (FixTypeModel fixTypeModel:fixTypeModelList) {
            FixTypeModel fixType = new FixTypeModel();
            fixType.setObjectID(fixTypeModel.get_id().toString());
            fixType.setName(fixTypeModel.getName());
            fixType.setTime(fixTypeModel.getTime());
            fixTypeModels.add(fixType);
        }
        return fixTypeModels.size()>0?fixTypeModels:null;
    }
}
