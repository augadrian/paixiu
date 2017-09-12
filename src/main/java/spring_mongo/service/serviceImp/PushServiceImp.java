package spring_mongo.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.PushDao;
import spring_mongo.model.EngineerModel;
import spring_mongo.model.PushModel;
import spring_mongo.service.PushService;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
@Service
@Transactional
public class PushServiceImp implements PushService {
    @Autowired
    private PushDao pushDao;

    @Override
    public List<PushModel> findPushByUserId(String userId) {
        return pushDao.findPushByUserId(userId);
    }

    @Override
    public void insertPush(PushModel pushModel) {
        pushDao.insertPush(pushModel);
    }

    @Override
    public PushModel updatePush(String userId, String name, String value) {
        return pushDao.updatePush(userId, name, value);
    }

    @Override
    public PushModel getByUserId(String userId) {
        return pushDao.getByUserId(userId);
    }
}
