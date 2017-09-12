package spring_mongo.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring_mongo.dao.SysUserDao;
import spring_mongo.model.SysUserModel;
import spring_mongo.service.SysUserService;

/**
 * Created by guxiaowei on 2017/8/30.
 */
@Service
@Transactional
public class SysUserServiceImp implements SysUserService{
    @Autowired
    private SysUserDao sysUserDao;
    @Override
    public SysUserModel getByID(String id) {
        SysUserModel sysUserModel = sysUserDao.getByID(id);
        sysUserModel.setObjectID(sysUserModel.get_id().toString());
        return sysUserModel;
    }
}
