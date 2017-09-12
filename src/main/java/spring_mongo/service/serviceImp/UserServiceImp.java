package spring_mongo.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.UserDao;
import spring_mongo.model.UserModel;
import spring_mongo.service.UserService;

/**
 * Created by guxiaowei on 2017/7/6.
 */
@Service
@Transactional
public class UserServiceImp implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public UserModel updateUser(String id) {
        return userDao.updateUser(id);
    }

    @Override
    public UserModel getUserById(String id) {
        return userDao.getByID(id);
    }

}
