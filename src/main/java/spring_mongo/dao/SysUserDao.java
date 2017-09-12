package spring_mongo.dao;

import org.springframework.transaction.annotation.Transactional;
import spring_mongo.model.SysUserModel;

/**
 * Created by guxiaowei on 2017/8/30.
 */
@Transactional
public interface SysUserDao {
    public SysUserModel getByID(String id);
}
